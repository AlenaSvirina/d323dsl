job ("MNTLAB-asvirina-main-build-job") {
 
     parameters {
         choiceParam('BRANCH_NAME', ['asvirina', 'master'], 'Branch name')
         activeChoiceParam('BUILDS_TRIGGER') {
             description('Select job')
             choiceType('CHECKBOX')
             groovyScript {
                 script('''return ["MNTLAB-asvirina-child1-build-job",
                         "MNTLAB-asvirina-child2-build-job",
                         "MNTLAB-asvirina-child3-build-job",
                         "MNTLAB-asvirina-child4-build-job"]''')
             }
         }
     }

	 steps {
         downstreamParameterized {
             trigger('$BUILDS_TRIGGER') {
                 block {
                     buildStepFailure('FAILURE')
                     failure('FAILURE')
                     unstable('UNSTABLE')
                 }
                 parameters {
                     currentBuildParameters()
                 }
             }
         }
     }
 }
 
 for (i in (1..4)) {
        job("MNTLAB-asvirina-child${i}-build-job") {
             scm {
				git {
					remote {
						url('https://github.com/AlenaSvirina/d323dsl.git')
					}
					branch('$BRANCH_NAME')
				}
			}         	
            
            parameters {
                activeChoiceReactiveParam('BRANCH_NAME') {
                    description('Select branch')
					choiceType('SINGLE_SELECT')
					groovyScript {
						script('''def gettags = ("git ls-remote -h https://github.com/AlenaSvirina/d323dsl.git").execute()
return gettags.text.readLines().collect { 
  it.split()[1].replaceAll('refs/heads/', '').replaceAll('refs/tags/', '')}''')
                    }
                }
            }
			steps {
                shell('''bash script.sh > output.txt
				tar -czf ${BRANCH_NAME}_dsl_script.tar.gz *''')
            }
				publishers {
					archiveArtifacts {
						pattern('**')
						onlyIfSuccessful(false)
                    }
				}
        }
 }
