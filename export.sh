source="D:\#Users\Gunnar\IntelliJ\CustomCrafting_2.0\target"
target="D:\Minecraft\PluginTestServer\\"$1"\plugins\customcrafting.jar"

cd $source
latest_file=$(ls -t | head -n 1)
cp -p "$latest_file" "$target"

