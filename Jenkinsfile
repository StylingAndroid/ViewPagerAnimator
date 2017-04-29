node {
  stage('Checkout') {
        checkout scm
  }

  stage('Build') {
        sh "./gradlew clean assemble test check"
  }

  stage('Report') {
        androidLint canComputeNew: false, defaultEncoding: '', failedTotalAll: '0', healthy: '', pattern: '**/lint-results.xml', unHealthy: '', unstableTotalAll: '0'
        step([$class: 'CheckStylePublisher', canComputeNew: false, defaultEncoding: '', failedTotalAll: '0', healthy: '', pattern: '**/reports/**/checkstyle.xml', unHealthy: '', unstableTotalAll: '0'])
        step([$class: 'FindBugsPublisher', canComputeNew: false, defaultEncoding: '', excludePattern: '', failedTotalAll: '0', healthy: '', includePattern: '', pattern: '**/reports/**/findbugs.xml', unHealthy: '', unstableTotalAll: '0'])
        step([$class: 'PmdPublisher', canComputeNew: false, defaultEncoding: '', failedTotalAll: '0', healthy: '', pattern: '**/reports/**/pmd.xml', unHealthy: '', unstableTotalAll: '0'])
        junit '**/test-results/*Debug*/**/*.xml'
  }
}
