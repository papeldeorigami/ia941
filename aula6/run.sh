java -jar ws3d-dist/WorldServer3D.jar &
echo "Wait 3 seconds to initialize the virtual creature controller..."
sleep 3
cd DemoJSOAR
java -jar ./dist/DemoJSOAR.jar &
echo "Wait 10 seconds to start the second virtual creature controller..."
sleep 10
java -jar ./dist/DemoJSOAR.jar no-reset
