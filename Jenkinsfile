#!groovy​

node {
    parameters {
                string(name: 'version', defaultValue: "${VERSION}", description: '')

                }

       stage('Build Distributables') {
            parallel {
                node('build && java8 && windows10 && msbuild ') {

                        checkout scm
                        bat  "gradlew.bat clean publishDistributablePublicationToMavenRepository -Pversion=${version}"

                }
                node('build && java8 && centos6 && gradle') {
                        checkout scm
                        sh '''export VERSION=$(./gradlew | grep VERSION | sed "s/VERSION/version/")
                              ./gradlew clean publishDistributablePublicationToMavenRepository -Pversion=${VERSION}'''

                }
                node('build && java8 && osx-10.12 && gradle) {
                                    checkout scm
                                    sh '''export VERSION=$(./gradlew | grep VERSION | sed "s/VERSION/version/")
                                          ./gradlew clean publishDistributablePublicationToMavenRepository -Pversion=${VERSION}'''

                                }

        }
        }
        stage('Build Jar') {
            node('build && java8 && centos6') {
            checkout scm
            sh '''export VERSION=$(./gradlew | grep VERSION | sed "s/VERSION/version/")
                  ./gradlew clean publishAllPlatformsJarPublicationToMavenRepository -Pversion=${VERSION}'''

        }


    }
    }
