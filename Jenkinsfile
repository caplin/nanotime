pipeline {
    agent none

    stages {
        stage('Get Version') {
            agent {
                label 'build && java8 && centos6'
                }
               steps {
               checkout scm
                sh '''echo (./gradlew | grep VERSION | sed "s/VERSION/version/") > pipeline.properties
                      '''
                      }
                script{
                      VERSION=readFile('pipeline.properties')
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
                        bat  "gradlew.bat clean publishDistributablePublicationToMavenRepository -Pversion=${VERSION}"
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
    }
}