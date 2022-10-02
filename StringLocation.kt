import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import java.util.*
import kotlin.collections.HashMap

class StringLocation {

    var worldName: String
        private set
    var x = 0.0
        private set
    var y = 0.0
        private set
    var z = 0.0
        private set
    var yaw = 0f
        private set
    var pitch = 0f
        private set

    constructor(text: String) {
        if (isStringLocation(text)) {
            throw InvalidPropertiesFormatException("Can't read location keys and values while trying to create string location!")
        }
        val keyAndValues = HashMap<String, String>()
        for (entry in text.split(",".toRegex())) {
            val keyAndValue = entry.split(":".toRegex())
            keyAndValues[keyAndValue[0]] = keyAndValue[1]
        }
        try {
            Bukkit.getWorld(keyAndValues["world"]!!)
        } catch (exception: NullPointerException) {
            throw NullPointerException("Can't found world while trying to create string location!")
        }
        try {
            worldName = checkNotNull(Bukkit.getWorld(keyAndValues["world"]!!)) { "Defined world is null!" }.name
            x = keyAndValues["x"]!!.toDouble()
            y = keyAndValues["y"]!!.toDouble()
            z = keyAndValues["z"]!!.toDouble()
            yaw = if (keyAndValues.containsKey("yaw")) keyAndValues["yaw"]!!.toFloat() else 0f
            pitch = if (keyAndValues.containsKey("pitch")) keyAndValues["pitch"]!!.toFloat() else 0f
        } catch (exception: NumberFormatException) {
            throw NumberFormatException("Invalid location value numbers while trying to create string location!")
        }
    }

    constructor(location: Location) {
        worldName = checkNotNull(location.world) { "World of the location is null" }.name
        x = location.x
        y = location.y
        z = location.z
        yaw = location.yaw
        pitch = location.pitch
    }

    constructor(worldName: String, x: Double, y: Double, z: Double, yaw: Float, pitch: Float) {
        this.worldName = worldName
        this.x = x
        this.y = y
        this.z = z
        this.yaw = yaw
        this.pitch = pitch
    }

    constructor(worldName: String, x: Double, y: Double, z: Double) {
        this.worldName = worldName
        this.x = x
        this.y = y
        this.z = z
        yaw = 0f
        pitch = 0f
    }

    fun toLocation(): Location {
        return Location(
            Bukkit.getWorld(worldName),
            x,
            y,
            z,
            yaw,
            pitch
        )
    }

    fun toText(): String {
        return "world:$worldName,x:$x,y:$y,z:$z,yaw:$yaw,pitch:$pitch"
    }

    fun setWorldName(world: World): StringLocation {
        worldName = world.name
        return this
    }

    fun setX(x: Double): StringLocation {
        this.x = x
        return this
    }

    fun setY(y: Double): StringLocation {
        this.y = y
        return this
    }

    fun setZ(z: Double): StringLocation {
        this.z = z
        return this
    }

    fun setYaw(yaw: Float): StringLocation {
        this.yaw = yaw
        return this
    }

    fun setPitch(pitch: Float): StringLocation {
        this.pitch = pitch
        return this
    }

    companion object {
        fun isStringLocation(textLocation: String): Boolean {
            if (textLocation.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size < 3) return false
            for (s in listOf("x", "y", "z", "yaw", "pitch")) {
                if (!textLocation.contains(s)) return false
            }
            return true
        }
    }

}
