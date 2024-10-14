# API REST com Spring Boot do Zero à AWS com Terraform e GitHub Actions

Obrigado por aparecer!

**Este conteúdo não é para iniciantes!** 😊

Neste vídeo, vou mostrar como criar uma **API REST** com **Spring Boot** do zero, com autenticação utilizando **Spring Security**, e implantá-la na **AWS** utilizando **Terraform** e **Github Actions**. Começaremos gerando a aplicação com o Spring Initializr, usando **Java 21** no **Visual Studio Code** com Maven. Em seguida, vamos containerizar a aplicação com Docker e preparar a infraestrutura com Terraform CDK em TypeScript, configurando um **Cluster ECS com Fargate** para o deployment. Por fim, automatizaremos todo o processo de integração contínua com **Github Actions**.  **#VAMOSCODAR**

## Descrição

Este é o repositório do vídeo **API REST com Spring Boot do Zero à AWS com Terraform e GitHub Actions**.  

✅ [ASSISTA O VÍDEO](https://youtu.be/vpHFGPZdiiw) 🚀🚀🚀🚀

Se gostou, não se esqueça de **se inscrever no canal, deixar like e compartilhar com outros devs**.

## Sumário

- [Ferramentas Necessárias](#ferramentas-necessárias)
- [Clonar o Projeto](#clonar-o-projeto)
- [Iniciar o Projeto](#iniciar-o-projeto)
- [Executar os Testes](#executar-os-testes)
- [Deploy da Infraestrutura](#deploy-da-infraestrutura)
- [Dúvidas](#dúvidas)

## Ferramentas Necessárias

Certifique-se de ter as seguintes ferramentas instaladas:

- **Node Version Manager (NVM)**
```bash
  curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.1/install.sh | bash
```

- **Node.js versão 20**
```bash
  nvm install 20
  nvm use 20
```

- **Java 21**
[Download](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)

Escolha a versão correspondente ao seu sistema. Caso utilize linux, macOS, ou vai instalar no WSL, preferir as versões tar.gz para uma instalação manual.

No windows, baixe os executáveis e faça a instalação normal "avançar, avançar, avançar". No linux, WSL ou MacOS, baixe os arquivos tar.gz e descompacte-os na pasta `/opt` do sistema. Caso você faça pelo WSL, o java só estará disponível internamente no WSL e não no Windows como um todo, é recomendável instalar nos dois ambientes.

Abra o arquivo `~/.zshrc` ou `~/.bash_profile` (a depender do terminal q você usa) e adicione as seguintes linhas no final do arquivo. Lembrar de colocar os valores corretos para o caminho de instalação do java em cada linha.

```bash
export JAVA_HOME='(caminho de instalação do java que você deseja que seja o padrão do seu sistema)'
export PATH=$PATH:$JAVA_HOME/bin
```

Para testar se deu tudo certo, você pode digitar o comando abaixo

```bash
java -version
```
A saída deve ser algo do tipo

```bash
java version "21.0.1" 2023-10-17 LTS
Java(TM) SE Runtime Environment (build 21.0.1+12-LTS-29)
Java HotSpot(TM) 64-Bit Server VM (build 21.0.1+12-LTS-29, mixed mode, sharing)
```

- **Maven**
Faça o download da versão 3.9.9 do Maven e descompacte rodando o comando abaixo.
```
sudo wget -c https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz -O - | sudo tar -xz -C /opt && sudo mv /opt/apache-maven-3.9.9-bin /opt/maven
```

Abra o arquivo `~/.zshrc` ou `~/.bash_profile` (a depender do terminal q você usa) e adicione a seguinte linha no final do arquivo

```bash
export PATH=$PATH:/opt/maven/bin
```
Caso já tenha uma linha semelhante anterior você poderá concatenar os valores da seguinte forma:

```bash
export PATH=$PATH:$JAVA_HOME/bin:/opt/maven/bin
```

Salve e feche o arquivo. O comando mvn já deve ficar disponível da próxima vez que você abrir o terminal. Para conferir digite o comando abaixo:

```bash
mvn -version
```

```bash
Apache Maven 3.8.1 (05c21c65bdfed0f71a2f2ada8b84da59348c4c5d)
Maven home: /opt/maven
Java version: 17.0.2, vendor: GraalVM Community, runtime: /Library/Java/JavaVirtualMachines/graalvm-ce-java17-21.3.1/Contents/Home
Default locale: en_BR, platform encoding: UTF-8
OS name: "mac os x", version: "10.15.7", arch: "x86_64", family: "mac"
```

- **AWS CLI**
[Documentação de Instalação](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)

- **DynamoDB Local**
```bash
docker run -d -p 8000:8000 amazon/dynamodb-local
```

- **DynamoDB Admin**
```bash
npm install -g dynamodb-admin
```

- **Terraform**
[Documentação de Instalação](https://developer.hashicorp.com/terraform/tutorials/aws-get-started/install-cli)

- **CDKTF**
```bash
npm install --global cdktf-cli@latest
```

## Clonar o Projeto
Clone o repositório para a sua máquina local:

```bash
git clone git@github.com:petrusdemelodev/003-api-rest-com-spring-boot.git
cd 003-api-rest-com-spring-boot
```

## Iniciar o Projeto
Navegue até a pasta da API, instale as dependências e inicie a aplicação:

```bash
cd api
mvn clean install
mvn spring-boot:run
```

## Executar os Testes
Para rodar os testes, execute:

```bash
mvn test
```

## Deploy da Infraestrutura
Para provisionar a infraestrutura na AWS, execute:

```bash
cd infra
npm install
cdktf apply dev
```
Para o comando acima, você deve ter credenciais válidas no arquivo `~/.aws/credentials`

# Dúvidas

Deixe seu comentário no vídeo! 😊

Se este repositório foi útil para você, por favor, deixe uma estrela ⭐ nele no GitHub. Isso ajuda a divulgar o projeto e motiva a criação de mais conteúdos como este.

# Redes Sociais

Me segue nas redes sociais

[INSTAGRAM](https://instagram.com/petrusdemelodev) | [LINKEDIN](https://linkedin.com/in/petrusdemelo) | [TWITTER](https://x.com/petrusdemelodev) | [MEDIUM](https://medium.com/@petrusdemelodev)