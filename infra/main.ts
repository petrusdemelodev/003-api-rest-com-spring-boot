import { App } from "cdktf";
import { ProjectStack } from "./lib/Project.stack";

const app = new App();

new ProjectStack(app, "dev", {
  environmentName: "dev",
  region: "us-east-1",
});

app.synth();
