package cc.sukazyo.cono.morny.social.xhs;

import cc.sukazyo.cono.morny.extra.xhs.XHSLink;
import org.openjdk.jmh.annotations.*;

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 5, time = 5)
@Measurement(iterations = 5, time = 5)
public class Benchmark_XHS_MatchOrSearch {
	
	public String SHARE_TEXT_PC = "\"24 【这个长得很像晓美焰的角色到底是谁..？ - 正义少女天使酱 | 小红书 - 你的生活指南】 \\uD83D\\uDE06 ghMU496vOiKgPGk \\uD83D\\uDE06 http://xhslink.com/59LkLS\"";
	public String NORMAL_TEXTS_SHORT = ".map{ case x => ... }\n" + "相当于\n" + ".map{ f => f match { case x => ... } }";
	public String NORMAL_TEXTS_LONG = "关于巴黎奥运开幕式，以下引用一些群友们的话：\n" + "\n" + "这次，跨性别者被禁止参赛的同时，变装皇后被拽上了舞台。马克龙政权真的有权挪用这些解放符号与否我不知道，但国内博主的时评却着实让我从另一个方角疯狂下头。\n" + "\n" + "这些时评每一条都在强调着「正确的秩序应当如何」，但这一套关于「什么是正确」的概念反而是非常带有殖民色彩的，几乎就是19世纪白人的主流世界观。比如，在性的论述里对种族肤色的特别关切（「杂交」！）、对非异性配偶的恐惧之类。这或许也解释了为什么同一群人基本上会认为「欧洲历史部分其实还可以」，但是当代社会却让他们这么抓狂。这些社会焦虑反而很白人特色，没什么中国本土感。底下那位大哥甚至能直接替基督教天主教被亵渎感到丢脸。\n" + "\n" + "从这个角度来分析的话，类似的思想本质上或许不是排外反西方，而是有点类似「崖山之后无中华」的精神在，是还向往着当初被强制开国时候的19世纪列强社会，却发现那个世界已经被颠覆了，于是只好退而自诩真正现代文明之精神传人。这反而正是一种殖民病，一种被殖民者对成为旧时代殖民者的强烈欲望，觉得只有那样才算文明。 在巴黎奥运没有跨性别能够参与[1]的情况下，已有两位顺性别女性运动员被造谣为指派男性[2]，鉴跨的最后是伤害所有人。\n" + "transphobia kills everyone\n" + "\n" + "[1] https://www.thenation.com/article/society/trans-athletes-paris-olympics/\n" + "[2] https://x.com/dw_chinese/status/1819189497613242683";
	
	@Benchmark
	public scala.collection.immutable.List<XHSLink.ShareLink> searchShareTexts_onMatch () {
		return XHSLink.searchShareText(SHARE_TEXT_PC);
	}
	@Benchmark
	public scala.collection.immutable.List<XHSLink.ShareLink> searchShareTexts_onMismatch_shortTexts () {
		return XHSLink.searchShareText(NORMAL_TEXTS_SHORT);
	}
	@Benchmark
	public scala.collection.immutable.List<XHSLink.ShareLink> searchShareTexts_onMismatch_longTexts () {
		return XHSLink.searchShareText(NORMAL_TEXTS_LONG);
	}
	
	@Benchmark
	public scala.collection.immutable.List<XHSLink.ShareLink> searchShareLinks_onMatch () {
		return XHSLink.searchShareUrl(SHARE_TEXT_PC);
	}
	@Benchmark
	public scala.collection.immutable.List<XHSLink.ShareLink> searchShareLinks_onMismatch_shortTexts () {
		return XHSLink.searchShareUrl(NORMAL_TEXTS_SHORT);
	}
	@Benchmark
	public scala.collection.immutable.List<XHSLink.ShareLink> searchShareLinks_onMismatch_longTexts () {
		return XHSLink.searchShareUrl(NORMAL_TEXTS_LONG);
	}
	
}
