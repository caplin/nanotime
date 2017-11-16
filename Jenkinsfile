pipeline {
    agent none
    stages {
        stage('Build Distributables') {
            parallel {
                stage('Build Windows') {
                    agent {
                        label 'build && java8 && windows10 && msbuild'
                    }
                    steps {
                        checkout scm
                        bat  'gradlew.bat clean publishDistributablePublicationToMavenRepository'
                }
                }
                stage('Build Linux') {
                    agent {
                        label 'build && java8 && centos6'
                    }
                    steps {
                        checkout scm
                        sh  'gradlew clean publishDistributablePublicationToMavenRepository'

                    }
                }
                stage('Build Darwin) {
                                    agent {
                                        label 'build && java8 && osx-10.12'
                                    }
                                    steps {
                                        checkout scm
                                        sh  'gradlew clean publishDistributablePublicationToMavenRepository'
                                    }
                                }

        }
        stage('Build Jar') {
            agent {
                label 'build && java8 && windows7 && chrome '
        }
        steps {
            checkout scm
        }
        }
    }
}