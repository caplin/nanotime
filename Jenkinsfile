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
        git branch: 'master', url: 'https://stash.caplin.com/scm/releng/promotionscripts.git'
            steps {
            sh '''
                echo gitcheckouted stuff
                ```
            }

        }
    }
}