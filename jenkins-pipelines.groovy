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

                // 빌드된 docker container를 private registry ( harbor.softcamp.co.kr ) 에 푸쉬한다.
                stage('Docker Build & Push') {
                    container("docker") {
                        dockerApp = docker.build("secaas/${PROJECT_NAME}", "--no-cache -f Dockerfile .")
                        docker.withRegistry("${CONTAINER_REGISTRY_URL}", 'harbor') {
                            dockerApp.push("${VERSION}")
                            dockerApp.push("latest")
                        }
                    }
                }
            }
        }
