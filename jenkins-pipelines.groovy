def PROJECT_NAME = "ojt-minhyeok-authorization"
def label = "${PROJECT_NAME}"
podTemplate(
        label: label,
        containers: [
                containerTemplate(name: 'jnlp', image: 'jenkins/inbound-agent:latest', args: '${computer.jnlpmac} ${computer.name}'),
                containerTemplate(name: 'maven', image: 'maven:3.8.3-openjdk-17', command: "cat", ttyEnabled: true),
                containerTemplate(name: "docker", image: "docker", command: "cat", ttyEnabled: true),
                containerTemplate(name: "kubectl", image: "lachlanevenson/k8s-kubectl", command: "cat", ttyEnabled: true)
        ],
        volumes: [
                hostPathVolume(hostPath: "/var/run/docker.sock", mountPath: "/var/run/docker.sock"),
                persistentVolumeClaim(mountPath: '/root/.m2/repository', claimName: 'maven-repo-storage', readOnly: false)
        ]
)
        {
            node(label) {

                stage('Git Pull') {
                    checkout scm
                }

                stage('Docker Build & Push') {
                    container("docker") {

                        // pom.xml을 이용한 라이브러리 정보 추출
                        VERSION = readMavenPom().getVersion()

                        dockerApp = docker.build("secaas/${PROJECT_NAME}", "--no-cache -f Dockerfile .")
                        docker.withRegistry('https://scr.softcamp.co.kr', 'harbor') {
                            dockerApp.push("${VERSION}")
                            dockerApp.push("latest")
                        }
                    }
                }

                stage('Kubernetes Deploy') {
                    container("kubectl") {
                        CURRENT_VERSION = sh(script: "kubectl describe deployment ${PROJECT_NAME} -n ${KUBE_NAMESPACE} | grep Image: | awk -F ':' '{ print \$3 }'",
                                returnStdout: true).trim()

                        echo "Current Running Deployment Version : ${CURRENT_VERSION}"

                        if (VERSION == CURRENT_VERSION) {
                            // 빌드 버전과 현재 버전이 같으면 Re Deploy
                            echo "The currently running deployment version and build version are the same."

                            sh "kubectl rollout restart deploy ${PROJECT_NAME} -n ${KUBE_NAMESPACE}"
                        } else {
                            YAML_FILE = "jenkins-deploy.yml"

                            sh "sed -i 's/BUILD_NUMBER/${VERSION}/g' ${YAML_FILE}"
                            sh "sed -i 's/IMAGE_HOST/${KUBE_IMAGE_HOST}/g' ${YAML_FILE}"
                            sh "sed -i 's/KUBE_NAMESPACE/${KUBE_NAMESPACE}/g' ${YAML_FILE}"
                            sh "kubectl apply -f ${YAML_FILE}"
                        }
                    }
                }
            }
        }
