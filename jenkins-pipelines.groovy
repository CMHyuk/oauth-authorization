def PROJECT_NAME = "authorization"
def label = "${PROJECT_NAME}-${BUILD_NUMBER}"
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

                stage('Docker Build') {
                    container("docker") {
                        dockerApp = docker.build('secaas/authorization-minhyeok')
                    }
                }

                stage('Docker Push') {
                    container("docker") {
                        dir("${WORKSPACE}/${PROJECT_NAME}") {
                            docker.withRegistry('https://scr.softcamp.co.kr', 'harbor') {
                                dockerApp.push("latest")
                            }
                        }
                    }
                }

                if (VERSION == CURRENT_VERSION) {
                    // 빌드 버전과 현재 버전이 같으면 Re Deploy
                    echo "The currently running deployment version and build version are the same."

                    sh "kubectl rollout restart deploy ${PROJECT_NAME} -n ${KUBE_NAMESPACE}"
                } else {
                    // 빌드된 docker container를 사용하는 deployment를 배포를 한다.
                    stage('Kubernetes deployment pod') {
                        container("kubectl") {
                            sh "sed -i 's/BUILD_NUMBER/${BUILD_NUMBER}/g' jenkins-deploy.yml"
                            sh "kubectl apply -f jenkins-deploy.yml"
                        }
                    }
                }
            }
        }
