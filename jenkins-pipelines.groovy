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

//                stage('Build') {
//                    // pom.xml을 이용한 라이브러리 정보 추출
//                    VERSION = readMavenPom().getVersion()
//
//                    MAVEN_BUILD_OPT = "-Pdev clean verify package"
//                    MAVEN_BUILD_SKIP_JUNIT_TEST = "-Dmaven.test.skip=true"
//                    MAVEN_BUILD_DCHECK_OPT = "-Ddependency-check.skip=true"
//
//                    echo "Start BUILD ${PROJECT_NAME}:${VERSION} Build_Opt [${MAVEN_BUILD_OPT} ${MAVEN_BUILD_DCHECK_OPT} ${MAVEN_BUILD_SKIP_JUNIT_TEST}]"
//
//                    // Jenkins의 설정에서 configfile을 다운로드(기 생성된 정보를 이용한 Maven settings.xml을 다운로드)
//                    configFileProvider([configFile(fileId: '0d84db68-876c-4c67-a8bd-c39b36969427', targetLocation: 'settings.xml', variable: 'AZURE_SETTINGS')]) {
//                    }
//
//                    // maven docker 이미지에서 동작 시작
//                    container('maven') {
//                        // junit test에서 사용하는 cloud-oauth-service의 docker image를 다운로드 하기 위한 scr.softcamp.co.kr의 credential 정보 추가
//                        configFileProvider([configFile(fileId: '78c72fcb-fab2-4d25-a223-efe8f15c93ff', variable: 'DOCKER_AUTH_CONFIG')]) {
//                            sh """
//                                mkdir -p /root/.docker
//                                cat $DOCKER_AUTH_CONFIG > /root/.docker/config.json
//                            """
//                        }
//                    }
//                }

                VERSION = readMavenPom().getVersion()

                stage('Docker Build & Push') {
                    container("docker") {
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
