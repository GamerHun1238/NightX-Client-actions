package net.aspw.client.script.remapper

import net.aspw.client.Client
import net.aspw.client.util.ClientUtils
import net.aspw.client.util.misc.HttpUtils
import net.aspw.client.util.network.CheckConnection
import java.io.File

/**
 * A srg remapper
 *
 * @author CCBlueX
 */
object Remapper {

    private const val srgName = "stable_22"
    private val srgFile = File(Client.fileManager.dir, "mcp-$srgName.srg")

    private val fields: HashMap<String, HashMap<String, String>> = hashMapOf()
    private val methods: HashMap<String, HashMap<String, String>> = hashMapOf()

    /**
     * Load srg
     */
    fun loadSrg() {
        // Check if srg file is already downloaded
        if (srgFile.length() == 0L) {
            // Download srg file
            srgFile.createNewFile()

            ClientUtils.getLogger().info("[Remapper] Downloading $srgName srg...")
            HttpUtils.download(CheckConnection.srgFile, srgFile)
            ClientUtils.getLogger().info("[Remapper] Downloaded $srgName.")
        }

        // Load srg
        ClientUtils.getLogger().info("[Remapper] Loading srg...")
        parseSrg()
        ClientUtils.getLogger().info("[Remapper] Loaded srg.")
    }

    private fun parseSrg() {
        srgFile.readLines().forEach {
            val args = it.split(" ")

            when {
                it.startsWith("FD:") -> {
                    val name = args[1]
                    val srg = args[2]

                    val className = name.substring(0, name.lastIndexOf('/')).replace('/', '.')
                    val fieldName = name.substring(name.lastIndexOf('/') + 1)
                    val fieldSrg = srg.substring(srg.lastIndexOf('/') + 1)

                    if (!fields.contains(className))
                        fields[className] = hashMapOf()

                    fields[className]!![fieldSrg] = fieldName
                }

                it.startsWith("MD:") -> {
                    val name = args[1]
                    val desc = args[2]
                    val srg = args[3]

                    val className = name.substring(0, name.lastIndexOf('/')).replace('/', '.')
                    val methodName = name.substring(name.lastIndexOf('/') + 1)
                    val methodSrg = srg.substring(srg.lastIndexOf('/') + 1)

                    if (!methods.contains(className))
                        methods[className] = hashMapOf()

                    methods[className]!![methodSrg + desc] = methodName
                }
            }
        }
    }

    /**
     * Remap field
     */
    fun remapField(clazz: Class<*>, name: String): String {
        if (!fields.containsKey(clazz.name))
            return name

        return fields[clazz.name]!!.getOrDefault(name, name)
    }

    /**
     * Remap method
     */
    fun remapMethod(clazz: Class<*>, name: String, desc: String): String {
        if (!methods.containsKey(clazz.name))
            return name

        return methods[clazz.name]!!.getOrDefault(name + desc, name)
    }
}