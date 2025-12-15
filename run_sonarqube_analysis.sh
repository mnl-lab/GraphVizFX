mvn clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
  -Dsonar.projectKey=graphvizfx-sonarqube-project-key \
  -Dsonar.projectName='GraphVizFX' \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=sqp_1684b851ec6f19bb0f4c3cf738166959689877ff