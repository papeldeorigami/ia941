java -jar ws3d-dist/WorldServer3D.jar &
echo "Wait 3 seconds to initialize the virtual creature controller..."
sleep 3
cd DemoCST/dist
java -jar DemoCST.jar 2
