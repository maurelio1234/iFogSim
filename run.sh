echo "Compiling..."
export CP='.:../jars/*:../jars/commons-math3-3.5/*'

cd src

echo Classpath: $CP
find . -name "*.java" -print | xargs javac -cp $CP

echo "DONE"

echo "Running..."

java -classpath $CP org.fog.test.SimpleExample
