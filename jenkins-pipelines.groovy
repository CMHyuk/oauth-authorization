def PROJECT_NAME = "authorization"
def label = "${PROJECT_NAME}-${BUILD_NUMBER}"
podTemplate(
        label: label,
        containers: [
                containerTemplate(name: "docker", image: "docker", command: "cat", ttyEnabled: true)
        ],
        // 현재 파드 내부에서 docker를 사용하기 위한 볼륨 마운트
        volumes: [
                hostPathVolume(hostPath: "/var/run/docker.sock", mountPath: "/var/run/docker.sock")
        ]
)
        {
            node(label) {

                // 젠킨스 빌드에 설정된 git 주소를 사용하여 소스코드 가져오기
                stage('Git Pull') {
                    checkout scm
                }

                // docker container 빌드 ( Dockerfile에서 Multi Stage를 사용하여 Maven 빌드를 수행 )
                stage('Docker build') {
                    container("docker") {
                        dockerApp = docker.build('jenkins-authorization/jenkins-authorization')
                    }
                }
            }
        }
