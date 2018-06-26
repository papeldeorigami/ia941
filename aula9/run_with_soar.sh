java -jar ws3d-dist/WorldServer3D.jar &
echo "Wait 3 seconds to initialize the virtual creature controller..."
sleep 3
mono ./DemoClarion/ClarionApp/bin/Release/ClarionApp.exe &
sleep 5
java -jar ./soar/DemoJSOAR.jar 1 &
