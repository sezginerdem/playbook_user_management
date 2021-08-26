pipeline {
    agent any
    stages {
        stage("Pull Playbook from SCM") {
            steps {
                git 'https://github.com/sezginerdem/ansible'
            }
        }
        
        stage("Assign Variables into the Playbook") {
            steps {
                    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                            sh """sed -i 's/add_user/${ADD_USER}/' playbook.yml"""
                            sh """sed -i 's/user_group_name/${GROUP_NAME}/' playbook.yml"""
                            sh """cat > user_public_key.pub <<EOF
                            ${USER_PUBLIC_KEY}
                            """
                    }
                }
        }
                    
        stage ('stage 2'){
            steps{
                            sh """sed -i 's/remove_user/${REMOVE_USER}/' playbook.yml"""
                        }
                    }
        
        stage("Run Playbook") {
            steps {
                ansiblePlaybook disableHostKeyChecking: true, installation: 'ansible', playbook: './playbook.yml'
            }
        }
        
        stage("Cleaning Workspace") {
            steps {
                sh 'rm -rf ./*'
            }
        }
    }
}