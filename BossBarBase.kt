import dev.calcryf.api.util.Colorize
import org.bukkit.Bukkit
import org.bukkit.boss.*

class BossBarBase<ID> : Cloneable {

    var title: String
    var color: BarColor
    var style: BarStyle
    val flags: MutableSet<BarFlag>
    private val data: MutableMap<ID, BossBar> = mutableMapOf()
    
    fun registerData(id: ID): Boolean {
        return data.put(id, createBossBar()) != null
    }
    
    fun unregisterData(id: ID): Boolean {
        return data.remove(id) != null
    }
    
    fun containsData(id: ID): Boolean {
        return data.containsKey(id)
    }
    
    fun getData(id: ID): BossBar? {
        return data[id]
    }

    fun addFlag(flag: BarFlag) {
        flags.add(flag)
    }

    fun removeFlag(flag: BarFlag) {
        flags.remove(flag)
    }

    fun hasFlag(flag: BarFlag): Boolean {
        return flags.contains(flag)
    }
    
    private fun createBossBar(): BossBar {
        return Bukkit.createBossBar(title, color, style, *flags.toTypedArray())
    }

    fun reloadBossBarFromData() {
        data.values.forEach { bossBar ->
            bossBar.setTitle(Colorize.format(title))
            bossBar.color = color
            bossBar.style = style
            BarFlag.values().filter { bossBar.hasFlag(it) }.forEach { bossBar.removeFlag(it) }
            flags.forEach { bossBar.addFlag(it) }
        }
    }

    constructor(title: String, color: BarColor, style: BarStyle) {
        this.title = Colorize.format(title)
        this.color = color
        this.style = style
        this.flags = HashSet()
    }

    constructor(title: String, color: BarColor, style: BarStyle, vararg flags: BarFlag) {
        this.title = Colorize.format(title)
        this.color = color
        this.style = style
        this.flags = flags.toMutableSet()
    }

    constructor(bossBar: BossBar) {
        this.title = Colorize.format(bossBar.title)
        this.color = bossBar.color
        this.style = bossBar.style
        this.flags = BarFlag.values().filter { bossBar.hasFlag(it) }.toMutableSet()
    }

    public override fun clone(): BossBarStabilizer {
        return try {
            super.clone() as BossBarStabilizer
        } catch (e: CloneNotSupportedException) {
            throw AssertionError()
        }
    }

}
