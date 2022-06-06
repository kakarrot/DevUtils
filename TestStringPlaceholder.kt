import org.w3c.dom.Document
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.SAXException
import java.io.IOException
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

/**
* 可以检测 Android 多语言中 占位符数目是否正确
**/
object XMlChecker {

    //默认语言
    const val default = "values"

    val LIST = listOf(
        "values-ru",
        "values-es",
        "values-pt",
        "values-ja",
        "values-in",
        "values-ms",
        "values-th",
        "values-vi",
        "values-tr",
        "values-zh",
        "values-fa"
    )

    //正则匹配：%d, %s, %%, %.数字f， %数字$s
    private const val Regex_XX = "%\\d\\\$s|%\\d\\\$d|%s|%d|%%|%.\\df"

    fun scan(folder: String = "values"): Map<String, Int> {
        // 创建一个DocumentBuilderFactory对象
        val dbf = DocumentBuilderFactory.newInstance()
        val newMap = hashMapOf<String, Int>()
        try {
            // 创建一个DocumentBuilder对象
            val db = dbf.newDocumentBuilder()
            // 使用parse方法解析xml文件
            val document: Document =
                db.parse("你的项目根路径\\src\\main\\res\\${folder}\\strings.xml")
            val nodeList: NodeList = document.getElementsByTagName("string")
            println("xml 中包含有" + nodeList.length.toString() + "个 string 节点")
            for (i in 0 until nodeList.length) {
                // 通过item(i)获取一个string节点
                val node: Node = nodeList.item(i)
                // 获取一个string节点的所有属性
                val itemAttrs: NamedNodeMap = node.attributes
                var textContent = node.textContent
                var count = 0
                if (textContent.contains(Regex(Regex_XX))) {
                    //println(itemAttrs.getNamedItem("name").nodeValue)
                    //println(textContent)
                }
                while (textContent.contains(Regex(Regex_XX))) {
                    textContent = textContent.replaceFirst(Regex(Regex_XX), "#")
                    count++
                }
                newMap[itemAttrs.getNamedItem("name").nodeValue] = count
            }
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
        } catch (e: SAXException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return newMap
    }
}

fun main(args: Array<String>) {
    val defaultMap = XMlChecker.scan(XMlChecker.default)

    XMlChecker.LIST.forEach { lang ->
        val langMap = XMlChecker.scan(lang)

        println("$lang: 占位符信息")

        defaultMap.forEach { (t, u) ->
            if (defaultMap[t] != langMap[t] && langMap.containsKey(t)) {
                println("默认里面有 $u 个， $lang 里面有 ${langMap[t]} 个, id=$t")
            }
        }
        println("***********************************************")
    }
}
