import { CloudwatchLogGroup } from "@cdktf/provider-aws/lib/cloudwatch-log-group";
import { DataAwsIamPolicyDocument } from "@cdktf/provider-aws/lib/data-aws-iam-policy-document";
import { DataAwsSsmParameter } from "@cdktf/provider-aws/lib/data-aws-ssm-parameter";
import { DataAwsSubnets } from "@cdktf/provider-aws/lib/data-aws-subnets";
import { DataAwsVpc } from "@cdktf/provider-aws/lib/data-aws-vpc";
import { DynamodbTable } from "@cdktf/provider-aws/lib/dynamodb-table";
import { EcrRepository } from "@cdktf/provider-aws/lib/ecr-repository";
import { EcsCluster } from "@cdktf/provider-aws/lib/ecs-cluster";
import { EcsService } from "@cdktf/provider-aws/lib/ecs-service";
import { EcsTaskDefinition } from "@cdktf/provider-aws/lib/ecs-task-definition";
import { IamRole } from "@cdktf/provider-aws/lib/iam-role";
import { IamRolePolicy } from "@cdktf/provider-aws/lib/iam-role-policy";
import { IamRolePolicyAttachment } from "@cdktf/provider-aws/lib/iam-role-policy-attachment";
import { Lb } from "@cdktf/provider-aws/lib/lb";
import { LbListener } from "@cdktf/provider-aws/lib/lb-listener";
import { LbListenerRule } from "@cdktf/provider-aws/lib/lb-listener-rule";
import { LbTargetGroup } from "@cdktf/provider-aws/lib/lb-target-group";
import { AwsProvider } from "@cdktf/provider-aws/lib/provider";
import { SecurityGroup } from "@cdktf/provider-aws/lib/security-group";
import { VpcSecurityGroupEgressRule } from "@cdktf/provider-aws/lib/vpc-security-group-egress-rule";
import { VpcSecurityGroupIngressRule } from "@cdktf/provider-aws/lib/vpc-security-group-ingress-rule";
import { Fn, TerraformStack } from "cdktf";
import { Construct } from "constructs";
import { ContainerDefinition } from "./types";

interface Config {
  environmentName: string;
  region: string;
}

export class ProjectStack extends TerraformStack {
  private readonly environment: string;
  private readonly dynamodb: DynamodbTable;
  private readonly secret: DataAwsSsmParameter;
  private readonly vpc: DataAwsVpc;
  private readonly subnets: DataAwsSubnets;

  constructor(scope: Construct, id: string, config: Config) {
    super(scope, id);

    this.environment = config.environmentName;

    new AwsProvider(this, "aws_provider", {
      region: config.region,
    });

    this.vpc = new DataAwsVpc(this, "vpc", {
      default: true,
    });

    this.subnets = new DataAwsSubnets(this, "subnets", {
      filter: [
        {
          name: "vpc-id",
          values: [this.vpc.id],
        }
      ]
    });

    this.dynamodb = new DynamodbTable(this, "dynamodb_table", {
      name: `${config.environmentName}-users`,
      hashKey: "id",
      attribute: [
        {
          name: "id",
          type: "S",
        },
        {
          name: "email",
          type: "S",
        },
      ],
      deletionProtectionEnabled: false,
      billingMode: "PAY_PER_REQUEST",
      globalSecondaryIndex: [
        {
          name: "email_index",
          hashKey: "email",
          projectionType: "ALL",
        }
      ]
    });

    const repository = new EcrRepository(this, "ecr_repository", {
      name: "se-inscreve-no-canal",
      imageScanningConfiguration: {
        scanOnPush: true,
      },
      imageTagMutability: 'MUTABLE',
    });

    const cloudwatch = new CloudwatchLogGroup(this, "cloudwatch_group",{
      name: "/aws/ecs/se-inscreve-no-canal",
      retentionInDays: 7,
    });

    this.secret = new DataAwsSsmParameter(this, "secret", {
      name: `${this.environment}-secret`,
    });

    const iamRole = this.createServiceIamRole();

    const containerDefinition: ContainerDefinition = {
      name: `${this.environment}-container`,
      image: `${repository.repositoryUrl}:latest`,
      cpu: 512,
      memory: 1024,
      essential: true,
      portMappings: [
        {
          containerPort: 8080,
          hostPort: 8080,
          protocol: "tcp",
        }
      ],
      environment: [
        {
          name: 'TABLE_NAME',
          value: this.dynamodb.name,
        },
        {
          name: 'AWS_REGION',
          value: config.region,
        }
      ],
      secrets: [
        {
          name: 'JWT_SECRET',
          valueFrom: this.secret.arn,
        }
      ],
      logConfiguration: {
        logDriver: "awslogs",
        options: {
          'awslogs-group': cloudwatch.name,
          'awslogs-region': config.region,
          'awslogs-stream-prefix': "ecs",
        }
      },
      healthCheck: {
        command: [
          "CMD-SHELL",
          "curl -f http://localhost:8080/actuator/health || exit 1"
        ],
        interval: 30,
        timeout: 5,
        retries: 3,
        startPeriod: 60,
      }
    };

    const taskDefinition = new EcsTaskDefinition(this, "task_definition", {
      family: `${this.environment}-task`,
      containerDefinitions: Fn.jsonencode([containerDefinition]),
      requiresCompatibilities: ["FARGATE"],
      cpu: "512",
      memory: "1024",
      networkMode: "awsvpc",
      executionRoleArn: iamRole.arn,
      taskRoleArn: iamRole.arn,
    });

    const cluster = new EcsCluster(this, "cluster", {
      name: `${this.environment}-cluster`,
      setting: [
        {
          name: "containerInsights",
          value: "enabled",
        }
      ],
      configuration: {
        executeCommandConfiguration: {
          logging: "OVERRIDE",
          logConfiguration: {
            cloudWatchLogGroupName: cloudwatch.name,
            cloudWatchEncryptionEnabled: false,
          }
        }
      }
    });

    const tg = this.createLoadBalancerAndGetTg();
    this.createEcsService(taskDefinition, cluster, tg);
  }

  private createServiceIamRole(): IamRole{ 
    const role = new IamRole(this, "service_role", {
      name: `${this.environment}ServiceRole`,
      assumeRolePolicy: new DataAwsIamPolicyDocument(this, "assume_role_policy", {
        statement: [
          {
            effect: "Allow",
            actions: ["sts:AssumeRole"],
            principals: [
              {
                type: "Service",
                identifiers: ["ecs-tasks.amazonaws.com"],
              }
            ]
          }
        ]
      }).json
    });

    new IamRolePolicyAttachment(this, "service_role_policy_execution", {
      policyArn: "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy",
      role: role.name,
    });

    new IamRolePolicy(this, "service_role_policy", {
      role: role.name,
      policy: new DataAwsIamPolicyDocument(this, "service_role_policy_document", {
        statement: [
          {
            actions: [
              'dynamodb:BatchGetItem',
              'dynamodb:BatchWriteItem',
              'dynamodb:ConditionCheckItem',
              'dynamodb:PutItem',
              'dynamodb:DescribeTable',
              'dynamodb:DeleteItem',
              'dynamodb:GetItem',
              'dynamodb:Scan',
              'dynamodb:Query',
              'dynamodb:UpdateItem',
            ],
            resources: [
              this.dynamodb.arn,
              `${this.dynamodb.arn}/*`
            ]
          },
          {
            resources: [
              this.secret.arn,
              `${this.secret.arn}/*`
            ],
            actions: [
              "ssm:Get*",
            ]
          }
        ]
      }).json
    });

    return role;
  }

  private createLoadBalancerAndGetTg(): LbTargetGroup {
    const sg = new SecurityGroup(this, 'lb_sg', {
      name: `${this.environment}-lb-sg`,
      vpcId: this.vpc.id,
    });

    new VpcSecurityGroupIngressRule(this, 'lb_sg_rule', {
      securityGroupId: sg.id,
      fromPort: 80,
      toPort: 80,
      ipProtocol: '6',// TPC,
      cidrIpv4: '0.0.0.0/0',
    });

    new VpcSecurityGroupEgressRule(this, 'lb_sg_egress_rule', {
      securityGroupId: sg.id,
      fromPort: 0,
      toPort: 0,
      ipProtocol: '-1',
      cidrIpv4: '0.0.0.0/0'
    });

    const lb = new Lb(this, "load_balancer", {
      name: `${this.environment}-lb`,
      internal: false,
      loadBalancerType: "application",
      securityGroups: [sg.id],
      subnets: this.subnets.ids,
    });

    const listener = new LbListener(this, "listener", {
      port: 80,
      protocol: "HTTP",
      loadBalancerArn: lb.arn,
      defaultAction: [
        {
          type: "fixed-response",
          fixedResponse: {
            statusCode: "404",
            contentType: "text/plain",
            messageBody: "Not found",
          }
        }
      ]
    });

    const targetGroup = new LbTargetGroup(this, "target_group", {
      vpcId: this.vpc.id,
      targetType: 'ip',
      port: 8080,
      protocol: 'HTTP',
      healthCheck: {
        enabled: true,
        interval: 30,
        path: '/actuator/health',
        port: 'traffic-port',
      }
    });
    
    new LbListenerRule(this, "listener_rule", {
      listenerArn: listener.arn,
      action: [
        {
          type: "forward",
          targetGroupArn: targetGroup.arn,
        }
      ],
      condition: [
        {
          pathPattern: {
            values: ["/*"],
          },
        },
      ]
    });
    
    return targetGroup;
  }

  private createEcsService(
    taskDefinition: EcsTaskDefinition, 
    cluster: EcsCluster, 
    targetGroup: LbTargetGroup): void {
    const sg = new SecurityGroup(this, 'ecs_sg', {
      name: `${this.environment}-ecs-sg`,
      vpcId: this.vpc.id,
    });

    new VpcSecurityGroupIngressRule(this, 'ecs_sg_rule', {
      securityGroupId: sg.id,
      fromPort: 8080,
      toPort: 8080,
      ipProtocol: '6',// TPC,
      cidrIpv4: '0.0.0.0/0',
    });

    new VpcSecurityGroupEgressRule(this, 'ecs_sg_egress_rule', {
      securityGroupId: sg.id,
      fromPort: 0,
      toPort: 0,
      ipProtocol: '-1',
      cidrIpv4: '0.0.0.0/0'
    });
    
    new EcsService(this, "service", {
      name: `${this.environment}-service`,
      cluster: cluster.arn,
      propagateTags: 'TASK_DEFINITION',
      launchType: "FARGATE",
      taskDefinition: taskDefinition.arn,
      desiredCount: 1,
      networkConfiguration: {
        subnets: this.subnets.ids,
        securityGroups: [sg.id],
        assignPublicIp: true,
      },
      loadBalancer: [
        {
          targetGroupArn: targetGroup.arn,
          containerName: `${this.environment}-container`,
          containerPort: 8080,
        }
      ],
      deploymentCircuitBreaker: {
        enable: true,
        rollback: true
      }
    });
  }
}