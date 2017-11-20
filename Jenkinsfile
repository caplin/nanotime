pipeline {
    agent none

    parameters {
                string(name: 'version', defaultValue: "${VERSION}", description: '')
                }
    stages {
        stage('Build Distributables') {
            parallel {
                stage('Build Windows') {
                    agent {
                        label 'build && java8 && windows10 && msbuild'
                    }
                    steps {
                        checkout scm
                        bat  "gradlew.bat clean publishDistributablePublicationToMavenRepository -Pversion=${version}"
                }
                }
                stage('Build Linux') {
                    agent {
                        label 'build && java8 && centos6'
                    }
                    steps {
                        checkout scm
                        sh '''export VERSION=$(./gradlew | grep VERSION | sed "s/VERSION/version/")
                              ./gradlew clean publishDistributablePublicationToMavenRepository -Pversion=${VERSION}'''
                    }
                }
                stage('Build Darwin') {
                      agent {
                           label 'build && java8 && osx-10.12'
                             }
                             steps {
                                    checkout scm
                                    sh '''export VERSION=$(./gradlew | grep VERSION | sed "s/VERSION/version/")
                                          ./gradlew clean publishDistributablePublicationToMavenRepository -Pversion=${VERSION}'''
                                    }
                                }

        }
        }
        stage('Build Jar') {
            agent {
                label 'build && java8 && centos6'
        }
        steps {
            checkout scm
            sh '''export VERSION=$(./gradlew | grep VERSION | sed "s/VERSION/version/")
                  ./gradlew clean publishAllPlatformsJarPublicationToMavenRepository -Pversion=${VERSION}'''
        }
        }
        stage("Promote to RC") {
            agent {
                label 'jenkins-cent7-004.caplin.com'
            }
                parameters {
                            string(name: 'REPO', defaultValue: "RC", description: '')
                            string(name: 'GROUP', defaultValue: "com.caplin.platform.components.codelibrary.nanotime", description: '')
                            string(name: 'BRANCH', defaultValue: "", description: '')
                            }
            steps {
            git credentialsId: 'f5d48fb8-f02a-4b63-afbf-ce46c50d9363', url: 'https://stash.caplin.com/scm/releng/promotionscripts.git'
            sh '''
                export VERSION=$(./gradlew | grep VERSION | sed "s/VERSION/version/")
                      ./gradlew clean PromoteToCaplinRC -Pversion=${VERSION}'''
                '''
            }
        }
    }
}