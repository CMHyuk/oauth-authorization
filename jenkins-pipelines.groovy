def PROJECT_NAME = "authorization"
def label = "${PROJECT_NAME}-${BUILD_NUMBER}"
podTemplate(
        label: label,
        containers: [
                containerTemplate(name: 'maven', image: 'maven:3.8.3-openjdk-17', command: "cat", ttyEnabled: true),
                containerTemplate(name: "docker", image: "docker", command: "cat", ttyEnabled: true)
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
                                dockerApp.push("${VERSION}")
                                dockerApp.push("latest")
                            }
                        }
                    }
                }

//                // 기존에 배포되어 동작중이던 deployment를 삭제한다.
//                try{
//                    stage('Kubernetes destroy exist pod & service') {
//                        container("kubectl") {
//                            sh "kubectl delete deployments authorization-minhyeok -n jenkins"
//                            sh "kubectl delete service authorization-minhyeok-service -n jenkins"
//                        }
//                    }
//                } catch(e) {
//                    sh "echo Does not exist kubernetes pod, service"
//                }

                // 빌드된 docker container를 사용하는 deployment를 배포를 한다.
                stage('Kubernetes deployment pod') {
                    container("kubectl") {
                        sh "sed -i 's/BUILD_NUMBER/${VERSION}/g' jenkins-deploy.yml"
                        sh "kubectl apply -f jenkins-deploy.yml"
                    }
                }
            }
        }
