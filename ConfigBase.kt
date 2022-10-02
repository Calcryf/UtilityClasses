import com.google.common.base.Charsets
import dev.calcryf.manhunt.Manhunt
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.*
import java.net.URL
import java.util.logging.Level

class ConfigBase(private val dataFolder: File, name: String) {

    private var newConfig: FileConfiguration? = null
    private val configFile: File

    init {
        dataFolder.mkdirs()
        configFile = File(dataFolder, "$name.yml")
    }

    val config: FileConfiguration
        get() {
            if (newConfig == null) {
                reloadConfig()
            }
            return newConfig!!
        }

    private fun getTextResource(file: String): Reader? {
        val `in` = getResource(file)
        return if (`in` == null) null else InputStreamReader(`in`, Charsets.UTF_8)
    }

    fun reloadConfig() {
        newConfig = YamlConfiguration.loadConfiguration(configFile)
        val defConfigStream = getResource(configFile.name) ?: return
        (newConfig as YamlConfiguration).setDefaults(
            YamlConfiguration.loadConfiguration(
                InputStreamReader(
                    defConfigStream,
                    Charsets.UTF_8
                )
            )
        )
    }

    fun saveConfig() {
        try {
            config.save(configFile)
        } catch (ex: IOException) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save config to $configFile", ex)
        }
    }

    fun saveDefaultConfig() {
        if (!configFile.exists()) {
            saveResource(configFile.name, false)
        }
    }

    fun saveResource(resourcePath_: String, replace: Boolean) {
        var resourcePath = resourcePath_
        require(resourcePath != "") { "ResourcePath cannot be null or empty" }
        resourcePath = resourcePath.replace('\\', '/')
        val `in` = getResource(resourcePath)
            ?: throw IllegalArgumentException("The embedded resource '$resourcePath' cannot be found in Main plugin.")
        val outFile = File(dataFolder, resourcePath)
        val lastIndex = resourcePath.lastIndexOf('/')
        val outDir = File(dataFolder, resourcePath.substring(0, lastIndex.coerceAtLeast(0)))
        if (!outDir.exists()) {
            outDir.mkdirs()
        }
        try {
            if (!outFile.exists() || replace) {
                val out: OutputStream = FileOutputStream(outFile)
                val buf = ByteArray(1024)
                var len: Int
                while (`in`.read(buf).also { len = it } > 0) {
                    out.write(buf, 0, len)
                }
                out.close()
                `in`.close()
            } else {
                Bukkit.getLogger().log(
                    Level.WARNING,
                    "Could not save " + outFile.name + " to " + outFile + " because " + outFile.name + " already exists."
                )
            }
        } catch (ex: IOException) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + outFile.name + " to " + outFile, ex)
        }
    }

    fun getResource(filename: String): InputStream? {
        return try {
            val url: URL = Manhunt.instance.javaClass.classLoader.getResource(filename) ?: return null
            val connection = url.openConnection()
            connection.useCaches = false
            connection.getInputStream()
        } catch (ex: IOException) {
            null
        }
    }

    companion object {
        fun generate(folder: File, name: String): ConfigBase {
            return ConfigBase(folder, name)
        }
    }
}
