package cc.aoeiuv020.panovel.data

import cc.aoeiuv020.base.jar.notNull
import cc.aoeiuv020.irondb.Iron
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

/**
 * Created by AoEiuV020 on 2018.06.12-17:27:05.
 */
class LocalManagerTest {
    private val local = LocalManager(Iron.db(File("/tmp/panovel/test/local")))
    private val zxcsText
        get() = """
==========================================================
更多精校小说尽在知轩藏书下载：http://www.zxcs8.com/
==========================================================
与千年女鬼同居的日子
作者：卜非

内容简介
　　为了赚点零花钱代人扫墓，结果一只女鬼跟着回了家，额滴个神呀，从此诡异的事情接二连三的发生在了自己身边。
　　红衣夜女杀人案、枯井中的无脸之人、河中的人形怪物……
　　更为奇怪的是，那些平时连想都不敢想的女神都主动凑了过来。
　　虽然这只女鬼长得俊俏又漂亮，可等知道她的真正身份之后，我和我的小伙伴顿时都惊呆了。


第一卷 红衣夜女


第1章 女鬼来了
　　黄昏时分，燕京郊区，西山墓园边的一块规划坟地。
　　天空飘着清明时节特有的断魂小雨，远处有几只乌鸦呱呱的啼叫。
　　整个墓园，空无一人。
　　但是山下的小路上，却走来一个急匆匆的少年。少年一米八出头的个子，穿着灰色连帽上衣，挽着裤腿，看起来二十左右岁的模样，手里还提着一个巨大的方便袋子。
　　只见少年走进坟场，一番东张西望之后，朝着西南方走来，最后停在一座矮坟之前。
　　矮坟不大，上面的泥土还散发着一股新鲜的气息，像是最近才翻盖的一般。几根小草从矮坟中冒出了头。


第2章 驱鬼
　　刘浪第一次真正体会到了什么是叫天天不应，叫地地不灵。
　　“咯咯，你可真有意思，我不是鬼，难道还是人不成？”
　　韩晓琪小嘴一嘟，勾起了两个小酒窝，一脸撒娇的模样，说多可爱有多可爱。
　　可此时刘浪哪里会觉得她可爱？只是吓的脸色苍白，喉咙干涩，想动，却动不了，浑身像是被使了紧箍咒一般。
　　“你、你到底想干嘛？我、跟你无冤无仇，你干嘛来找我啊！”
　　刘浪都快要吓尿了。


第二卷 僵尸之殇


第82章 报仇
　　红衣女鬼终于被搞定了，可是，刘浪的心却再也无法平静了。
　　女鬼韩晓琪告诉刘浪，这个世界上存在着三本书，而这三本书都有自己的传人，分别代表着相、命、道三术。
　　相术传人强调卜算推演，命术传人讲究延命续魂，而道术传人，最为厉害的就是道术与符咒之术。
　　传说中这三人手中都有一本书，而三本书中都有着一部分秘密，三本书合而为一之后，可以找到一本阴阳书。
　　这阴阳书就是破除韩家诅咒的关键。
==========================================================
更多精校小说尽在知轩藏书下载：http://www.zxcs8.com/
==========================================================
"""


    @Test
    fun importText() {
        zxcsText.byteInputStream().use { input ->
            local.importText(input, Charsets.UTF_8)
        }
        assertEquals("卜非", local.author)
        assertEquals("与千年女鬼同居的日子", local.name)
        val chapters = local.chapters.notNull()
        assertEquals(5, chapters.size)
        chapters.first().let {
            assertEquals("第一卷 红衣夜女", it.name)
            val content = local.getContent(it.extra)
            assertEquals(0, content.size)
        }
        chapters[1].let {
            assertEquals("第1章 女鬼来了", it.name)
            val content = local.getContent(it.extra)
            assertEquals("黄昏时分，燕京郊区，西山墓园边的一块规划坟地。", content.first())
            assertEquals("矮坟不大，上面的泥土还散发着一股新鲜的气息，像是最近才翻盖的一般。几根小草从矮坟中冒出了头。", content.last())
            assertEquals(6, content.size)
        }
        chapters.last().let {
            assertEquals("第82章 报仇", it.name)
            val content = local.getContent(it.extra)
            assertEquals("红衣女鬼终于被搞定了，可是，刘浪的心却再也无法平静了。", content.first())
            assertEquals("这阴阳书就是破除韩家诅咒的关键。", content.last())
            assertEquals(5, content.size)
        }
    }
}