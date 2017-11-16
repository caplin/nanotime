pipeline {
    agent none

    parameters {
                string(name: 'version', defaultValue: "${VERSION}", description: '')
                }
    stages {

        stage ('Get Version') {
            agent {
                   label 'build && java8 && centos6'
                   }
                   steps {
                           checkout scm
                             sh  'export VERSION  | ./gradlew --info | grep VERSION | sed "s/VERSION/version/"'
                             sh "echo ${params.version}""
                         }
        }


        stage('Build Distributables') {
            parallel {
                stage('Build Windows') {
                    agent {
                        label 'build && java8 && windows10 && msbuild'
                    }
                    steps {
                        checkout scm
                        bat  "gradlew.bat clean publishDistributablePublicationToMavenRepository -Pversion=${params.version}"
                }
                }
                stage('Build Linux') {
                    agent {
                        label 'build && java8 && centos6'
                    }
                    steps {
                        checkout scm
                        sh 'echo ${params.version}'
                        sh  './gradlew clean publishDistributablePublicationToMavenRepository -Pversion=${params.version}'

                    }
                }
                stage('Build Darwin') {
                      agent {
                           label 'build && java8 && osx-10.12'
                             }
                             steps {
                                    checkout scm
                                    sh  './gradlew clean publishDistributablePublicationToMavenRepository -Pversion=${params.version}'
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
            bat 'gradlew.bat clean publishAllPlatformsJarPublicationToMavenRepository'
        }
        }
    }
}