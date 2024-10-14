export interface ContainerDefinition {
	name: string;
	image: string;
	repositoryCredentials?: RepositoryCredentials;
	cpu: number;
	memory: number;
	memoryReservation?: number;
	links?: string[];
	portMappings: PortMapping[];
	essential?: boolean;
	entryPoint?: string[];
	command?: string[];
	environment?: Environment[];
	environmentFiles?: EnvironmentFile[];
	mountPoints?: MountPoint[];
	volumesFrom?: VolumesFrom[];
	linuxParameters?: LinuxParameters;
	secrets?: Secret[];
	dependsOn?: DependsOn[];
	startTimeout?: number;
	stopTimeout?: number;
	hostname?: string;
	user?: string;
	workingDirectory?: string;
	disableNetworking?: boolean;
	privileged?: boolean;
	readonlyRootFilesystem?: boolean;
	dnsServers?: string[];
	dnsSearchDomains?: string[];
	extraHosts?: ExtraHost[];
	dockerSecurityOptions?: string[];
	interactive?: boolean;
	pseudoTerminal?: boolean;
	dockerLabels?: Record<string, string>;
	ulimits?: Ulimit[];
	logConfiguration?: LogConfiguration;
	healthCheck?: HealthCheck;
	systemControls?: SystemControl[];
	resourceRequirements?: EnvironmentFile[];
	firelensConfiguration?: FirelensConfiguration;
}

export interface DependsOn {
	containerName: string;
	condition: string;
}

export interface Environment {
	name: string;
	value: string;
}

export interface EnvironmentFile {
	value: string;
	type: string;
}

export interface ExtraHost {
	hostname: string;
	ipAddress: string;
}

export interface FirelensConfiguration {
	type: string;
	options?: Record<string, string>;
}

export interface HealthCheck {
	command: string[];
	interval: number;
	timeout: number;
	retries: number;
	startPeriod: number;
}

export interface LinuxParameters {
	capabilities: Capabilities;
	devices: Device[];
	initProcessEnabled: boolean;
	sharedMemorySize: number;
	tmpfs: Tmpf[];
	maxSwap: number;
	swappiness: number;
}

export interface Capabilities {
	add: string[];
	drop: string[];
}

export interface Device {
	hostPath: string;
	containerPath: string;
	permissions: string[];
}

export interface Tmpf {
	containerPath: string;
	size: number;
	mountOptions: string[];
}

export interface LogConfiguration {
	logDriver: string;
	options?: Record<string, string>;
	secretOptions?: Secret[];
}

export interface Secret {
	name: string;
	valueFrom: string;
}

export interface MountPoint {
	sourceVolume: string;
	containerPath: string;
	readOnly: boolean;
}

export interface PortMapping {
	containerPort: number;
	hostPort: number;
	protocol?: string;
}

export interface RepositoryCredentials {
	credentialsParameter: string;
}

export interface SystemControl {
	namespace: string;
	value: string;
}

export interface Ulimit {
	name: string;
	softLimit: number;
	hardLimit: number;
}

export interface VolumesFrom {
	sourceContainer: string;
	readOnly: boolean;
}
