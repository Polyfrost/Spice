package wtf.zani.spice.debug

object DebugHelper {
    internal val sections = mutableListOf<DebugSection>()

    @JvmStatic
    fun applyExtraDebugInfo(lines: MutableList<String>) {
        sections.forEach { section ->
            lines += ""

            section
                .lines
                .forEach {
                    lines += "§5[Spice]§r ${it.invoke()}"
                }
        }
    }
}
