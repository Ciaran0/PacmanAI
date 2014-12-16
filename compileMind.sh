javac -cp "*" WizardPMind.java
jar cf0 WizardPMind.jar WizardPMind.class
java -cp "*" org.w2mind.toolkit.Main -mind WizardPMind -world WizardPWorld	-g
