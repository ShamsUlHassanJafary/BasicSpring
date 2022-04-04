pipeline {
    agent any
    stages {
        stage('compile') {
            steps {
                echo 'pull from git'
                echo 'build'
                sh "sudo mvn clean package -Pprod"

                sh "sudo cp target/gms-4-u-0.0.1-SNAPSHOT.jar /etc/ansible/teknigarage/gms-4-u-0.0.1-SNAPSHOT.jar"
                sh "sudo cp configs/deployment.yaml /etc/ansible/teknigarage/deployment.yaml"
                sh "sudo cp configs/hosts /etc/ansible/teknigarage/hosts"
                sh "sudo cp configs/env_variables /etc/ansible/teknigarage/env_variables"


                //sh "sudo ansible all -m ping -i /etc/ansible/teknigarage/hosts  --private-key=/home/tekadmin/id_rsa"

                //sh "sudo ansiblePlaybook credentialsId: '/home/tekadmin/id_rsa', inventory: '/etc/ansible/teknigarage/hosts', playbook: '/etc/ansible/teknigarage/deployment.yaml'

                sh "sudo ansible-playbook /etc/ansible/teknigarage/deployment.yaml -i /etc/ansible/teknigarage/hosts --private-key=/home/tekadmin/id_rsa"

               // ansiblePlaybook(
               //             credentialsId: '/home/tekadmin/id_rsa',
               //             inventory: '/etc/ansible/teknigarage/hosts',
               //             playbook: '/etc/ansible/teknigarage/deployment.yaml'
               //            )
            }

        }


    }

}

