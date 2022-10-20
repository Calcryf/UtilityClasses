import org.bukkit.Bukkit
import org.bukkit.boss.*

/**
 * @author ensyasir@gmail.com
 */
class BossBarStabilizer : Cloneable {

    var title: String
    var color: BarColor
    var style: BarStyle
    val flags: MutableSet<BarFlag>
    val bossBar: BossBar

    fun addFlag(flag: BarFlag) {
        flags.add(flag)
    }

    fun removeFlag(flag: BarFlag) {
        flags.remove(flag)
    }

    fun hasFlag(flag: BarFlag): Boolean {
        return flags.contains(flag)
    }

    fun reloadBossBarFromData() {
        bossBar.setTitle(Colorize.format(title))
        bossBar.color = color
        bossBar.style = style
        BarFlag.values().filter { bossBar.hasFlag(it) }.forEach { bossBar.removeFlag(it) }
        flags.forEach { bossBar.addFlag(it) }
    }

    constructor(title: String, color: BarColor, style: BarStyle) {
        this.title = Colorize.format(title)
        this.color = color
        this.style = style
        this.flags = HashSet()
        this.bossBar = Bukkit.createBossBar(title, color, style)
    }

    constructor(title: String, color: BarColor, style: BarStyle, vararg flags: BarFlag) {
        this.title = Colorize.format(title)
        this.color = color
        this.style = style
        this.flags = flags.toMutableSet()
        this.bossBar = Bukkit.createBossBar(this.title, this.color, this.style, *flags)
    }

    constructor(bossBar: BossBar) {
        this.title = Colorize.format(bossBar.title)
        this.color = bossBar.color
        this.style = bossBar.style
        this.flags = BarFlag.values().filter { bossBar.hasFlag(it) }.toMutableSet()
        this.bossBar = Bukkit.createBossBar(this.title, this.color, this.style, *flags.toTypedArray())
    }

    public override fun clone(): BossBarStabilizer {
        return try {
            super.clone() as BossBarStabilizer
        } catch (e: CloneNotSupportedException) {
            throw AssertionError()
        }
    }

}
