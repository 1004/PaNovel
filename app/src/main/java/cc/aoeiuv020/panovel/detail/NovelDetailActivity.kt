@file:Suppress("DEPRECATION")

package cc.aoeiuv020.panovel.detail

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import cc.aoeiuv020.panovel.App
import cc.aoeiuv020.panovel.IView
import cc.aoeiuv020.panovel.R
import cc.aoeiuv020.panovel.api.NovelChapter
import cc.aoeiuv020.panovel.api.NovelDetail
import cc.aoeiuv020.panovel.api.NovelItem
import cc.aoeiuv020.panovel.local.Settings
import cc.aoeiuv020.panovel.local.toBean
import cc.aoeiuv020.panovel.local.toJson
import cc.aoeiuv020.panovel.share.Share
import cc.aoeiuv020.panovel.sql.entity.RequesterData
import cc.aoeiuv020.panovel.text.NovelTextActivity
import cc.aoeiuv020.panovel.util.alert
import cc.aoeiuv020.panovel.util.alertError
import cc.aoeiuv020.panovel.util.getStringExtra
import cc.aoeiuv020.panovel.util.show
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdListener
import kotlinx.android.synthetic.main.activity_novel_detail.*
import kotlinx.android.synthetic.main.activity_novel_detail.view.*
import kotlinx.android.synthetic.main.content_novel_detail.*
import org.jetbrains.anko.*

/**
 *
 * Created by AoEiuV020 on 2017.10.03-18:10:37.
 */
class NovelDetailActivity : AppCompatActivity(), IView, AnkoLogger {
    companion object {
        fun start(context: Context, requesterData: RequesterData) {
            TODO()
        }
        fun start(context: Context, novelItem: NovelItem) {
            context.startActivity<NovelDetailActivity>("novelItem" to novelItem.toJson())
        }
    }

    private lateinit var alertDialog: AlertDialog
    private lateinit var presenter: NovelDetailPresenter
    private lateinit var chapterAdapter: NovelChaptersAdapter
    private var novelDetail: NovelDetail? = null
    private lateinit var novelItem: NovelItem
    private var isRefreshEnable = false

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("novelItem", novelItem.toJson())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        alertDialog = AlertDialog.Builder(this).create()

        setContentView(R.layout.activity_novel_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        novelItem = getStringExtra("novelItem", savedInstanceState)?.toBean() ?: run {
            // 不应该会到这里，
            toast("奇怪，重新打开试试，")
            finish()
            return
        }
        val requester = novelItem.requester
        debug { "receive $requester" }

        chapterAdapter = NovelChaptersAdapter(this, novelItem)
        recyclerView.setAdapter(chapterAdapter)
        recyclerView.setLayoutManager(GridLayoutManager(this@NovelDetailActivity, 3))

        setTitle(novelItem)

        fabRead.setOnClickListener {
            NovelTextActivity.start(this, novelItem)
        }
        fabStar.setOnClickListener {
            fabStar.toggle()
            if (fabStar.isChecked) {
                presenter.addBookshelf(novelItem.requester)
            } else {
                presenter.removeBookshelf(novelItem.requester)
            }
        }

        swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }
        swipeRefreshLayout.setOnChildScrollUpCallback { _, _ -> !isRefreshEnable }
        app_bar.addOnOffsetChangedListener { _, verticalOffset ->
            isRefreshEnable = verticalOffset == 0
        }
        swipeRefreshLayout.isRefreshing = true

        presenter = NovelDetailPresenter(novelItem)
        presenter.attach(this)
        presenter.start()

        ad_view.adListener = object : AdListener() {
            override fun onAdLoaded() {
                ad_view.show()
            }
        }

        if (Settings.adEnabled) {
            ad_view.loadAd(App.adRequest)
        }
    }

    override fun onPause() {
        ad_view.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        ad_view.resume()
    }

    override fun onDestroy() {
        ad_view.destroy()
        presenter.detach()
        super.onDestroy()
    }

    override fun onRestart() {
        super.onRestart()
        chapterAdapter.refresh()
    }

    private fun setTitle(novelItem: NovelItem) {
        toolbar_layout.title = "${novelItem.name} - ${novelItem.author}"
    }

    fun showNovelDetail(detail: NovelDetail) {
        this.novelDetail = detail
        setTitle(detail.novel)
        Glide.with(this).load(detail.bigImg).into(toolbar_layout.image)
        presenter.requestChapters(detail.requester)
    }

    fun showNovelChaptersDesc(chapters: ArrayList<NovelChapter>) {
        chapterAdapter.data = chapters
        recyclerView.recyclerView.post {
            swipeRefreshLayout.isRefreshing = false
        }
    }

    fun showContainsBookshelf(contains: Boolean) {
        fabStar.isChecked = contains
    }

    fun showSharedUrl(url: String, qrCode: String) {
        Share.alert(this, url, qrCode)
    }

    fun showError(message: String, e: Throwable) {
        swipeRefreshLayout.isRefreshing = false
        alertError(alertDialog, message, e)
    }

    private fun showNovelAbout() {
        novelDetail?.let {
            alert(alertDialog, it.introduction, "${it.novel.name} - ${it.novel.author}")
        }
    }

    private fun refresh() {
        swipeRefreshLayout.isRefreshing = true
        presenter.refresh()
    }

    private fun share() {
        presenter.share()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.browse -> browse(novelItem.requester.url)
            R.id.info -> showNovelAbout()
            R.id.refresh -> refresh()
            R.id.share -> share()
            android.R.id.home -> onBackPressed()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

}

