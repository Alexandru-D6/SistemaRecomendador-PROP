EXEC_CLASS=Driver
GUI_CLASS=GUI
JUNIT_CLASS=JUnitDistance
ARGS=commands.txt

SOURCE_DIR=FONT
BINARY_DIR=EXE
LIB_DIR=LIB

EXEC_SRC_NAME=$(SOURCE_DIR)/$(EXEC_CLASS).java
EXEC_NAME=$(BINARY_DIR)/$(EXEC_CLASS).class
JUNIT_SRC_NAME=$(SOURCE_DIR)/TEST/$(JUNIT_CLASS).java
JUNIT_NAME=$(BINARY_DIR)/$(JUNIT_CLASS).class

JUNIT_JAR=$(LIB_DIR)/junit-4.12.jar
HAMCREST_JAR=$(LIB_DIR)/hamcrest-core-1.3.jar


run: $(EXEC_NAME)
	java -cp $(BINARY_DIR) $(EXEC_CLASS) $(ARGS)

$(EXEC_NAME): $(EXEC_SRC_NAME)
	javac -cp $(SOURCE_DIR) -d $(BINARY_DIR) $(EXEC_SRC_NAME)


junit: $(JUNIT_NAME)
	java -cp $(BINARY_DIR):$(JUNIT_JAR):$(HAMCREST_JAR) org.junit.runner.JUnitCore TEST.$(JUNIT_CLASS)

$(JUNIT_NAME): $(JUNIT_SRC_NAME)
	javac -cp $(SOURCE_DIR):$(JUNIT_JAR) -d $(BINARY_DIR) $(JUNIT_SRC_NAME)
	
gui:
	make run EXEC_CLASS=$(GUI_CLASS) ARGS=

test:
	make run EXEC_CLASS=TEST/$(TEST_CLASS)
	
clean:
	find ./$(BINARY_DIR) -name "*.class" -type f -delete
	find ./$(SOURCE_DIR) -name "*.class" -type f -delete
	


# git commands (ignore)

pull:
	git pull

push:
	git pull
	git add *
	git commit -a
	git push origin master
