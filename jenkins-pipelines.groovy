def PROJECT_NAME = "ojt_minhyeok_authorization"
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

                // 젠킨스 빌드에 설정된 git 주소를 사용하여 소스코드 가져오기
                stage('Git Pull') {
                    checkout scm
                }

                stage('Docker Build & Push') {
                    container("docker") {
                        dockerApp = docker.build("secaas/${PROJECT_NAME}", "--no-cache -f Dockerfile .")
                        docker.withRegistry('https://scr.softcamp.co.kr', 'harbor') {
                            dockerApp.push("latest")
                        }
                    }
                }

                stage('Kubernetes Deploy') {
                    container("kubectl") {
                        YAML_FILE = "jenkins-deploy.yml"

                        sh "sed -i 's/IMAGE_HOST/${KUBE_IMAGE_HOST}/g' ${YAML_FILE}"
                        sh "sed -i 's/KUBE_NAMESPACE/${KUBE_NAMESPACE}/g' ${YAML_FILE}"
                        sh "kubectl apply -f ${YAML_FILE}"
                    }
                }
            }
        }
