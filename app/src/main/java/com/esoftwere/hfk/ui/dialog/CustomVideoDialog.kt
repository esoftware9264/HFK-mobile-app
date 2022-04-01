package com.esoftwere.hfk.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.esoftwere.hfk.R
import com.esoftwere.hfk.databinding.DialogLayoutVideoBinding
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class CustomVideoDialog(
    context: Context, val videoUrl: String
) : Dialog(context),
    Player.EventListener {
    private lateinit var binding: DialogLayoutVideoBinding
    private lateinit var simpleExoplayer: SimpleExoPlayer

    private var bandwidthMeter: BandwidthMeter? = null
    private var trackSelector: TrackSelector? = null
    private var trackSelectionFactory: TrackSelection.Factory? = null
    private var extractorsFactory: ExtractorsFactory? = null
    private var defaultBandwidthMeter: DefaultBandwidthMeter? = null
    private var dataSourceFactory: DataSource.Factory? = null
    private var playbackPosition: Long = 0

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(getContext()),
            R.layout.dialog_layout_video, null, false
        )
        setContentView(binding.root)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            attributes = layoutParams
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        initView()
        initVariable()
        initializePlayer()
    }

    private fun initView() {
        binding.tvCloseDialog?.setOnClickListener {
            dismiss()
            releasePlayer()
        }
    }

    private fun initVariable() {
        bandwidthMeter = DefaultBandwidthMeter()
        extractorsFactory = DefaultExtractorsFactory()
        trackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        trackSelector = DefaultTrackSelector(trackSelectionFactory)
        defaultBandwidthMeter = DefaultBandwidthMeter()
        dataSourceFactory = DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, "HFKApplication"), defaultBandwidthMeter
        )
    }

    private fun initializePlayer() {
        simpleExoplayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)
        preparePlayer(videoUrl)
        binding.exoPlayerView.player = simpleExoplayer
        simpleExoplayer.seekTo(playbackPosition)
        simpleExoplayer.playWhenReady = true
        simpleExoplayer.addListener(this)
    }

    private fun buildMediaSource(url: String): MediaSource {
        val mediaSource = ExtractorMediaSource
            .Factory(dataSourceFactory)
            .setExtractorsFactory(extractorsFactory)
            .createMediaSource(Uri.parse(url))

        return mediaSource
    }

    private fun preparePlayer(videoUrl: String) {
        val mediaSource = buildMediaSource(videoUrl)
        simpleExoplayer.prepare(mediaSource)
    }

    private fun releasePlayer() {
        playbackPosition = simpleExoplayer.currentPosition
        simpleExoplayer.release()
    }

    internal fun setDialogTitle(title: String) {
        if (this::binding.isInitialized) {

        }
    }

    /**
     * Exoplayer Callback Implementation
     */
    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {

    }

    override fun onTracksChanged(
        trackGroups: TrackGroupArray?,
        trackSelections: TrackSelectionArray?
    ) {

    }

    override fun onLoadingChanged(isLoading: Boolean) {

    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == Player.STATE_BUFFERING)
            binding.progressBar.visibility = View.VISIBLE
        else if (playbackState == Player.STATE_READY || playbackState == Player.STATE_ENDED)
            binding.progressBar.visibility = View.INVISIBLE
    }

    override fun onRepeatModeChanged(repeatMode: Int) {

    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

    }

    override fun onPlayerError(error: ExoPlaybackException?) {

    }

    override fun onPositionDiscontinuity(reason: Int) {

    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {

    }

    override fun onSeekProcessed() {

    }
}