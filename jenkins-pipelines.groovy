def PROJECT_NAME = "authorization"
def label = "${PROJECT_NAME}-${BUILD_NUMBER}"
podTemplate(
        label: label,
        containers: [
                containerTemplate(name: 'maven', image: 'maven:3.8.3-openjdk-17', command: "cat", ttyEnabled: true),
                containerTemplate(name: "docker", image: "docker", command: "cat", ttyEnabled: true)
        ],
        // 현재 파드 내부에서 docker를 사용하기 위한 볼륨 마운트
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
            }
        }
