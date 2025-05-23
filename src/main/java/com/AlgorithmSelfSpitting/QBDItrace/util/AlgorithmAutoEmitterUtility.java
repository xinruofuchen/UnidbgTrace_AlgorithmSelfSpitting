package com.AlgorithmSelfSpitting.QBDItrace.util;


import com.AlgorithmSelfSpitting.QBDItrace.entity.AesSboxEntity;
import com.AlgorithmSelfSpitting.QBDItrace.entity.algorithmType;
import com.AlgorithmSelfSpitting.publicclass.publicEntity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

//自吐算法
public class AlgorithmAutoEmitterUtility {

    //SM4:
    private static String[] sm4List = {"a3b1bac6", "56aa3350", "677d9197", "b27022dc"};
    private static String[] sm4T0 = {"8ed55b5b","d0924242","4deaa7a7","06fdfbfb", "fccf3333","65e28787","c93df4f4","6bb5dede","4e165858","6eb4dada","44145050","cac10b0b","8828a0a0","17f8efef","9c2cb0b0","11051414","872bacac","fb669d9d","f2986a6a","ae77d9d9","822aa8a8","46bcfafa","14041010","cfc00f0f","02a8aaaa","54451111","5f134c4c","be269898","6d482525","9e841a1a","1e061818","fd9b6666","ec9e7272","4a430909","10514141","24f7d3d3","d5934646","53ecbfbf","f89a6262","927be9e9","ff33cccc","04555151","270b2c2c","4f420d0d","59eeb7b7","f3cc3f3f","1caeb2b2","ea638989","74e79393","7fb1cece","6c1c7070","0daba6a6","edca2727","28082020","48eba3a3","c1975656","80820202","a3dc7f7f","c4965252","12f9ebeb","a174d5d5","b38d3e3e","c33ffcfc","3ea49a9a","5b461d1d","1b071c1c","3ba59e9e","0cfff3f3","3ff0cfcf","bf72cdcd","4b175c5c","52b8eaea","8f810e0e","3d586565","cc3cf0f0","7d196464","7ee59b9b","91871616","734e3d3d","08aaa2a2","c869a1a1","c76aadad","85830606","7ab0caca","b570c5c5","f4659191","b2d96b6b","a7892e2e","18fbe3e3","47e8afaf","330f3c3c","674a2d2d","b071c1c1","0e575959","e99f7676","e135d4d4","661e7878","b4249090","360e3838","265f7979","ef628d8d","38596161","95d24747","2aa08a8a","b1259494","aa228888","8c7df1f1","d73becec","05010404","a5218484","9879e1e1","9b851e1e","84d75353","00000000","5e471919","0b565d5d","e39d7e7e","9fd04f4f","bb279c9c","1a534949","7c4d3131","ee36d8d8","0a020808","7be49f9f","20a28282","d4c71313","e8cb2323","e69c7a7a","42e9abab","43bdfefe","a2882a2a","9ad14b4b","40410101","dbc41f1f","d838e0e0","61b7d6d6","2fa18e8e","2bf4dfdf","3af1cbcb","f6cd3b3b","1dfae7e7","e5608585","41155454","25a38686","60e38383","16acbaba","295c7575","34a69292","f7996e6e","e434d0d0","721a6868","01545555","19afb6b6","df914e4e","fa32c8c8","f030c0c0","21f6d7d7","bc8e3232","75b3c6c6","6fe08f8f","691d7474","2ef5dbdb","6ae18b8b","962eb8b8","8a800a0a","fe679999","e2c92b2b","e0618181","c0c30303","8d29a4a4","af238c8c","07a9aeae","390d3434","1f524d4d","764f3939","d36ebdbd","81d65757","b7d86f6f","eb37dcdc","51441515","a6dd7b7b","09fef7f7","b68c3a3a","932fbcbc","0f030c0c","03fcffff","c26ba9a9","ba73c9c9","d96cb5b5","dc6db1b1","375a6d6d","15504545","b98f3636","771b6c6c","13adbebe","da904a4a","57b9eeee","a9de7777","4cbef2f2","837efdfd","55114444","bdda6767","2c5d7171","45400505","631f7c7c","50104040","325b6969","b8db6363","220a2828","c5c20707","f531c4c4","a88a2222","31a79696","f9ce3737","977aeded","49bff6f6","992db4b4","a475d1d1","90d34343","5a124848","58bae2e2","71e69797","64b6d2d2","70b2c2c2","ad8b2626","cd68a5a5","cb955e5e","624b2929","3c0c3030","ce945a5a","ab76dddd","867ff9f9","f1649595","5dbbe6e6","35f2c7c7","2d092424","d1c61717","d66fb9b9","dec51b1b","94861212","78186060","30f3c3c3","897cf5f5","5cefb3b3","d23ae8e8","acdf7373","794c3535","a0208080","9d78e5e5","56edbbbb","235e7d7d","c63ef8f8","8bd45f5f","e7c82f2f","dd39e4e4","68492121"};
    private static String[] sm4T1 = {"5b8ed55b","42d09242","a74deaa7","fb06fdfb", "33fccf33","8765e287","f4c93df4","de6bb5de","584e1658","da6eb4da","50441450","0bcac10b","a08828a0","ef17f8ef","b09c2cb0","14110514","ac872bac","9dfb669d","6af2986a","d9ae77d9","a8822aa8","fa46bcfa","10140410","0fcfc00f","aa02a8aa","11544511","4c5f134c","98be2698","256d4825","1a9e841a","181e0618","66fd9b66","72ec9e72","094a4309","41105141","d324f7d3","46d59346","bf53ecbf","62f89a62","e9927be9","ccff33cc","51045551","2c270b2c","0d4f420d","b759eeb7","3ff3cc3f","b21caeb2","89ea6389","9374e793","ce7fb1ce","706c1c70","a60daba6","27edca27","20280820","a348eba3","56c19756","02808202","7fa3dc7f","52c49652","eb12f9eb","d5a174d5","3eb38d3e","fcc33ffc","9a3ea49a","1d5b461d","1c1b071c","9e3ba59e","f30cfff3","cf3ff0cf","cdbf72cd","5c4b175c","ea52b8ea","0e8f810e","653d5865","f0cc3cf0","647d1964","9b7ee59b","16918716","3d734e3d","a208aaa2","a1c869a1","adc76aad","06858306","ca7ab0ca","c5b570c5","91f46591","6bb2d96b","2ea7892e","e318fbe3","af47e8af","3c330f3c","2d674a2d","c1b071c1","590e5759","76e99f76","d4e135d4","78661e78","90b42490","38360e38","79265f79","8def628d","61385961","4795d247","8a2aa08a","94b12594","88aa2288","f18c7df1","ecd73bec","04050104","84a52184","e19879e1","1e9b851e","5384d753","00000000","195e4719","5d0b565d","7ee39d7e","4f9fd04f","9cbb279c","491a5349","317c4d31","d8ee36d8","080a0208","9f7be49f","8220a282","13d4c713","23e8cb23","7ae69c7a","ab42e9ab","fe43bdfe","2aa2882a","4b9ad14b","01404101","1fdbc41f","e0d838e0","d661b7d6","8e2fa18e","df2bf4df","cb3af1cb","3bf6cd3b","e71dfae7","85e56085","54411554","8625a386","8360e383","ba16acba","75295c75","9234a692","6ef7996e","d0e434d0","68721a68","55015455","b619afb6","4edf914e","c8fa32c8","c0f030c0","d721f6d7","32bc8e32","c675b3c6","8f6fe08f","74691d74","db2ef5db","8b6ae18b","b8962eb8","0a8a800a","99fe6799","2be2c92b","81e06181","03c0c303","a48d29a4","8caf238c","ae07a9ae","34390d34","4d1f524d","39764f39","bdd36ebd","5781d657","6fb7d86f","dceb37dc","15514415","7ba6dd7b","f709fef7","3ab68c3a","bc932fbc","0c0f030c","ff03fcff","a9c26ba9","c9ba73c9","b5d96cb5","b1dc6db1","6d375a6d","45155045","36b98f36","6c771b6c","be13adbe","4ada904a","ee57b9ee","77a9de77","f24cbef2","fd837efd","44551144","67bdda67","712c5d71","05454005","7c631f7c","40501040","69325b69","63b8db63","28220a28","07c5c207","c4f531c4","22a88a22","9631a796","37f9ce37","ed977aed","f649bff6","b4992db4","d1a475d1","4390d343","485a1248","e258bae2","9771e697","d264b6d2","c270b2c2","26ad8b26","a5cd68a5","5ecb955e","29624b29","303c0c30","5ace945a","ddab76dd","f9867ff9","95f16495","e65dbbe6","c735f2c7","242d0924","17d1c617","b9d66fb9","1bdec51b","12948612","60781860","c330f3c3","f5897cf5","b35cefb3","e8d23ae8","73acdf73","35794c35","80a02080","e59d78e5","bb56edbb","7d235e7d","f8c63ef8","5f8bd45f","2fe7c82f","e4dd39e4","21684921"};
    private static String[] sm4T2 = {"5b5b8ed5","4242d092","a7a74dea","fbfb06fd", "3333fccf","878765e2","f4f4c93d","dede6bb5","58584e16","dada6eb4","50504414","0b0bcac1","a0a08828","efef17f8","b0b09c2c","14141105","acac872b","9d9dfb66","6a6af298","d9d9ae77","a8a8822a","fafa46bc","10101404","0f0fcfc0","aaaa02a8","11115445","4c4c5f13","9898be26","25256d48","1a1a9e84","18181e06","6666fd9b","7272ec9e","09094a43","41411051","d3d324f7","4646d593","bfbf53ec","6262f89a","e9e9927b","ccccff33","51510455","2c2c270b","0d0d4f42","b7b759ee","3f3ff3cc","b2b21cae","8989ea63","939374e7","cece7fb1","70706c1c","a6a60dab","2727edca","20202808","a3a348eb","5656c197","02028082","7f7fa3dc","5252c496","ebeb12f9","d5d5a174","3e3eb38d","fcfcc33f","9a9a3ea4","1d1d5b46","1c1c1b07","9e9e3ba5","f3f30cff","cfcf3ff0","cdcdbf72","5c5c4b17","eaea52b8","0e0e8f81","65653d58","f0f0cc3c","64647d19","9b9b7ee5","16169187","3d3d734e","a2a208aa","a1a1c869","adadc76a","06068583","caca7ab0","c5c5b570","9191f465","6b6bb2d9","2e2ea789","e3e318fb","afaf47e8","3c3c330f","2d2d674a","c1c1b071","59590e57","7676e99f","d4d4e135","7878661e","9090b424","3838360e","7979265f","8d8def62","61613859","474795d2","8a8a2aa0","9494b125","8888aa22","f1f18c7d","ececd73b","04040501","8484a521","e1e19879","1e1e9b85","535384d7","00000000","19195e47","5d5d0b56","7e7ee39d","4f4f9fd0","9c9cbb27","49491a53","31317c4d","d8d8ee36","08080a02","9f9f7be4","828220a2","1313d4c7","2323e8cb","7a7ae69c","abab42e9","fefe43bd","2a2aa288","4b4b9ad1","01014041","1f1fdbc4","e0e0d838","d6d661b7","8e8e2fa1","dfdf2bf4","cbcb3af1","3b3bf6cd","e7e71dfa","8585e560","54544115","868625a3","838360e3","baba16ac","7575295c","929234a6","6e6ef799","d0d0e434","6868721a","55550154","b6b619af","4e4edf91","c8c8fa32","c0c0f030","d7d721f6","3232bc8e","c6c675b3","8f8f6fe0","7474691d","dbdb2ef5","8b8b6ae1","b8b8962e","0a0a8a80","9999fe67","2b2be2c9","8181e061","0303c0c3","a4a48d29","8c8caf23","aeae07a9","3434390d","4d4d1f52","3939764f","bdbdd36e","575781d6","6f6fb7d8","dcdceb37","15155144","7b7ba6dd","f7f709fe","3a3ab68c","bcbc932f","0c0c0f03","ffff03fc","a9a9c26b","c9c9ba73","b5b5d96c","b1b1dc6d","6d6d375a","45451550","3636b98f","6c6c771b","bebe13ad","4a4ada90","eeee57b9","7777a9de","f2f24cbe","fdfd837e","44445511","6767bdda","71712c5d","05054540","7c7c631f","40405010","6969325b","6363b8db","2828220a","0707c5c2","c4c4f531","2222a88a","969631a7","3737f9ce","eded977a","f6f649bf","b4b4992d","d1d1a475","434390d3","48485a12","e2e258ba","979771e6","d2d264b6","c2c270b2","2626ad8b","a5a5cd68","5e5ecb95","2929624b","30303c0c","5a5ace94","ddddab76","f9f9867f","9595f164","e6e65dbb","c7c735f2","24242d09","1717d1c6","b9b9d66f","1b1bdec5","12129486","60607818","c3c330f3","f5f5897c","b3b35cef","e8e8d23a","7373acdf","3535794c","8080a020","e5e59d78","bbbb56ed","7d7d235e","f8f8c63e","5f5f8bd4","2f2fe7c8","e4e4dd39","21216849"};
    private static String[] sm4T3 = {"d55b5b8e","924242d0","eaa7a74d","fdfbfb06","cf3333fc","e2878765","3df4f4c9","b5dede6b","1658584e","b4dada6e","14505044","c10b0bca","28a0a088","f8efef17","2cb0b09c","05141411","2bacac87","669d9dfb","986a6af2","77d9d9ae","2aa8a882","bcfafa46","04101014","c00f0fcf","a8aaaa02","45111154","134c4c5f","269898be","4825256d","841a1a9e","0618181e","9b6666fd","9e7272ec","4309094a","51414110","f7d3d324","934646d5","ecbfbf53","9a6262f8","7be9e992","33ccccff","55515104","0b2c2c27","420d0d4f","eeb7b759","cc3f3ff3","aeb2b21c","638989ea","e7939374","b1cece7f","1c70706c","aba6a60d","ca2727ed","08202028","eba3a348","975656c1","82020280","dc7f7fa3","965252c4","f9ebeb12","74d5d5a1","8d3e3eb3","3ffcfcc3","a49a9a3e","461d1d5b","071c1c1b","a59e9e3b","fff3f30c","f0cfcf3f","72cdcdbf","175c5c4b","b8eaea52","810e0e8f","5865653d","3cf0f0cc","1964647d","e59b9b7e","87161691","4e3d3d73","aaa2a208","69a1a1c8","6aadadc7","83060685","b0caca7a","70c5c5b5","659191f4","d96b6bb2","892e2ea7","fbe3e318","e8afaf47","0f3c3c33","4a2d2d67","71c1c1b0","5759590e","9f7676e9","35d4d4e1","1e787866","249090b4","0e383836","5f797926","628d8def","59616138","d2474795","a08a8a2a","259494b1","228888aa","7df1f18c","3bececd7","01040405","218484a5","79e1e198","851e1e9b","d7535384","00000000","4719195e","565d5d0b","9d7e7ee3","d04f4f9f","279c9cbb","5349491a","4d31317c","36d8d8ee","0208080a","e49f9f7b","a2828220","c71313d4","cb2323e8","9c7a7ae6","e9abab42","bdfefe43","882a2aa2","d14b4b9a","41010140","c41f1fdb","38e0e0d8","b7d6d661","a18e8e2f","f4dfdf2b","f1cbcb3a","cd3b3bf6","fae7e71d","608585e5","15545441","a3868625","e3838360","acbaba16","5c757529","a6929234","996e6ef7","34d0d0e4","1a686872","54555501","afb6b619","914e4edf","32c8c8fa","30c0c0f0","f6d7d721","8e3232bc","b3c6c675","e08f8f6f","1d747469","f5dbdb2e","e18b8b6a","2eb8b896","800a0a8a","679999fe","c92b2be2","618181e0","c30303c0","29a4a48d","238c8caf","a9aeae07","0d343439","524d4d1f","4f393976","6ebdbdd3","d6575781","d86f6fb7","37dcdceb","44151551","dd7b7ba6","fef7f709","8c3a3ab6","2fbcbc93","030c0c0f","fcffff03","6ba9a9c2","73c9c9ba","6cb5b5d9","6db1b1dc","5a6d6d37","50454515","8f3636b9","1b6c6c77","adbebe13","904a4ada","b9eeee57","de7777a9","bef2f24c","7efdfd83","11444455","da6767bd","5d71712c","40050545","1f7c7c63","10404050","5b696932","db6363b8","0a282822","c20707c5","31c4c4f5","8a2222a8","a7969631","ce3737f9","7aeded97","bff6f649","2db4b499","75d1d1a4","d3434390","1248485a","bae2e258","e6979771","b6d2d264","b2c2c270","8b2626ad","68a5a5cd","955e5ecb","4b292962","0c30303c","945a5ace","76ddddab","7ff9f986","649595f1","bbe6e65d","f2c7c735","0924242d","c61717d1","6fb9b9d6","c51b1bde","86121294","18606078","f3c3c330","7cf5f589","efb3b35c","3ae8e8d2","df7373ac","4c353579","208080a0","78e5e59d","edbbbb56","5e7d7d23","3ef8f8c6","d45f5f8b","c82f2fe7","39e4e4dd","49212168"};
    //MD5:
    private static String[] md5List = {"67452301", "efcdaB89","98Badcfe","10325476"};
    //SHA1：
    private static String[] sha1List = {"c3d2e1f0"};//sha1和md5一样，这个常数是判断是否是sha1还是md5的标准
    //CRC32:
    private static String[] crc32List = {"00000000","77073096","ee0e612c","990951ba","076dc419","706af48f","e963a535","9e6495a3","0edb8832","79dcb8a4","e0d5e91e","97d2d988","09b64c2b","7eb17cbd","e7b82d07","90bf1d91","1db71064","6ab020f2","f3b97148","84be41de","1adad47d","6ddde4eb","f4d4b551","83d385c7","136c9856","646ba8c0","fd62f97a","8a65c9ec","14015c4f","63066cd9","fa0f3d63","8d080df5","3b6e20c8","4c69105e","d56041e4","a2677172","3c03e4d1","4b04d447","d20d85fd","a50ab56b","35b5a8fa","42b2986c","dbbbc9d6","acbcf940","32d86ce3","45df5c75","dcd60dcf","abd13d59","26d930ac","51de003a","c8d75180","bfd06116","21b4f4b5","56b3c423","cfba9599","b8bda50f","2802b89e","5f058808","c60cd9b2","b10be924","2f6f7c87","58684c11","c1611dab","b6662d3d","76dc4190","01db7106","98d220bc","efd5102a","71b18589","06b6b51f","9fbfe4a5","e8b8d433","7807c9a2","0f00f934","9609a88e","e10e9818","7f6a0dbb","086d3d2d","91646c97","e6635c01","6b6b51f4","1c6c6162","856530d8","f262004e","6c0695ed","1b01a57b","8208f4c1","f50fc457","65b0d9c6","12b7e950","8bbeb8ea","fcb9887c","62dd1ddf","15da2d49","8cd37cf3","fbd44c65","4db26158","3ab551ce","a3bc0074","d4bb30e2","4adfa541","3dd895d7","a4d1c46d","d3d6f4fb","4369e96a","346ed9fc","ad678846","da60b8d0","44042d73","33031de5","aa0a4c5f","dd0d7cc9","5005713c","270241aa","be0b1010","c90c2086","5768b525","206f85b3","b966d409","ce61e49f","5edef90e","29d9c998","b0d09822","c7d7a8b4","59b33d17","2eb40d81","b7bd5c3b","c0ba6cad","edb88320","9abfb3b6","03b6e20c","74b1d29a","ead54739","9dd277af","04db2615","73dc1683","e3630b12","94643b84","0d6d6a3e","7a6a5aa8","e40ecf0b","9309ff9d","0a00ae27","7d079eb1","f00f9344","8708a3d2","1e01f268","6906c2fe","f762575d","806567cb","196c3671","6e6b06e7","fed41b76","89d32be0","10da7a5a","67dd4acc","f9b9df6f","8ebeeff9","17b7be43","60b08ed5","d6d6a3e8","a1d1937e","38d8c2c4","4fdff252","d1bb67f1","a6bc5767","3fb506dd","48b2364b","d80d2bda","af0a1b4c","36034af6","41047a60","df60efc3","a867df55","316e8eef","4669be79","cb61b38c","bc66831a","256fd2a0","5268e236","cc0c7795","bb0b4703","220216b9","5505262f","c5ba3bbe","b2bd0b28","2bb45a92","5cb36a04","c2d7ffa7","b5d0cf31","2cd99e8b","5bdeae1d","9b64c2b0","ec63f226","756aa39c","026d930a","9c0906a9","eb0e363f","72076785","05005713","95bf4a82","e2b87a14","7bb12bae","0cb61b38","92d28e9b","e5d5be0d","7cdcefb7","0bdbdf21","86d3d2d4","f1d4e242","68ddb3f8","1fda836e","81be16cd","f6b9265b","6fb077e1","18b74777","88085ae6","ff0f6a70","66063bca","11010b5c","8f659eff","f862ae69","616bffd3","166ccf45","a00ae278","d70dd2ee","4e048354","3903b3c2","a7672661","d06016f7","4969474d","3e6e77db","aed16a4a","d9d65adc","40df0b66","37d83bf0","a9bcae53","debb9ec5","47b2cf7f","30b5ffe9","bdbdf21c","cabac28a","53b39330","24b4a3a6","bad03605","cdd70693","54de5729","23d967bf","b3667a2e","c4614ab8","5d681b02","2a6f2b94","b40bbe37","c30c8ea1","5a05df1b","2d02ef8d"};
    //AES：
    //todo:循环10次
    private static int[] AesForlenth = {10,12,14};
    public static String [] aesSbox =
            {"63","7c","77","7b","f2","6b","6f","c5","30","01","67","2b","fe","d7","ab","76",
             "ca","82","c9","7d","fa","59","47","f0","ad","d4","a2","af","9c","a4","72","c0",
             "b7","fd","93","26","36","3f","f7","cc","34","a5","e5","f1","71","d8","31","15",
             "04","c7","23","c3","18","96","05","9a","07","12","80","e2","eb","27","b2","75",
             "09","83","2c","1a","1b","6e","5a","a0","52","3b","d6","b3","29","e3","2f","84",
             "53","d1","00","ed","20","fc","b1","5b","6a","cb","be","39","4a","4c","58","cf",
             "d0","ef","aa","fb","43","4d","33","85","45","f9","02","7f","50","3c","9f","a8",
             "51","a3","40","8f","92","9d","38","f5","bc","b6","da","21","10","ff","f3","d2",
             "cd","0c","13","ec","5f","97","44","17","c4","a7","7e","3d","64","5d","19","73",
             "60","81","4f","dc","22","2a","90","88","46","ee","b8","14","de","5e","0b","db",
             "e0","32","3a","0a","49","06","24","5c","c2","d3","ac","62","91","95","e4","79",
             "e7","c8","37","6d","8d","d5","4e","a9","6c","56","f4","ea","65","7a","ae","08",
             "ba","78","25","2e","1c","a6","b4","c6","e8","dd","74","1f","4b","bd","8b","8a",
             "70","3e","b5","66","48","03","f6","0e","61","35","57","b9","86","c1","1d","9e",
             "e1","f8","98","11","69","d9","8e","94","9b","1e","87","e9","ce","55","28","df",
             "8c","a1","89","0d","bf","e6","42","68","41","99","2d","0f","b0","54","bb","16"};
    //DES：


    public static algorithmType AlgorithmRecognition(String args, Map argsval){
        String [] list = splitStringByComma(args);
        for(String item :list){
            // 定义正则表达式模式
            Pattern pattern = Pattern.compile("=(.*)");
            Matcher matcher = pattern.matcher(item);
            if (matcher.find()) {
                String result = matcher.group(1);
                if(result.length()==8){
                    algorithmType type = isalgorithm(result,argsval);
                    if (type != algorithmType.AlgorithmUNKNOWN){
//                        System.out.println("debugger");
                        return type;
                    }
                }
            }
        }
        return algorithmType.NOAlgorithm;
    }

    public static String AlgorithmRecognitionKey(String key,String args){
        String [] item = splitStringByComma(args);
        for (String item1 : item) {
            // 定义正则表达式模式
            Pattern pattern = Pattern.compile("=(.*)");
            Matcher matcher = pattern.matcher(item1);
            if (matcher.find()) {
                String result = matcher.group(1);
                if(!result.contains(key)){
//                    System.out.println(result);
                    return result;
                }
            }
        }

        return null;
    }

    public static ArrayList<String> key_list = new ArrayList(4);
    public static String superins = "";
    public static int superindex = 0;

    public static void initsuperins(){
        superins = "";
        superindex = 0;
    }
    public static algorithmType isalgorithm(String arg,Map argsval){

        for(String sm4item :sm4List){
            if( arg.equals(sm4item)){
                //常数

                if (argsval.get("instructions").toString().contains("eor")){
                    String keyitem = AlgorithmRecognitionKey(arg,argsval.get("argsVal").toString());
                    key_list.add(keyitem);
                    if (arg.equals("b27022dc")){
                        //最后一次算法，输出并清理所有key
                        String keyval = key_list.get(0)+key_list.get(1)+key_list.get(2)+key_list.get(3);
                        SaveLog.storeContentByLine("sm4 key:"+keyval+"\n", publicEntity.path+"/trace_algorithm.log");
                        key_list.clear();
                    }
                }
                return algorithmType.AlgorithmSM4;
            };
        }

            if( arg.equals("d76aa478")&&argsval.get("instructions").toString().contains("add")){ //add.*d76aa478
                SaveLog.storeContentByLine("md5T[0]:"+arg +" "+argsval.toString(),publicEntity.path+"/trace_algorithm.log");
            };

        for(String md5item :md5List){
            if( arg.equals(md5item)){
                if (argsval.get("instructions").toString().contains("eor")&&argsval.get("argsVal").toString().contains("10325476")&&argsval.get("argsVal").toString().contains("98badcfe")&&argsval.get("resultVal").toString().contains("88888888")){
                    SaveLog.storeContentByLine("\n\nthis is new md5  addr :"+argsval.get("deviation").toString() +"\nmd5 encode value addr in "+argsval.toString() +"\nhook this addr read or write"+"\n or argsVal equese d76aa478",publicEntity.path+"/trace_algorithm.log");
                }
                return algorithmType.AlgorithmMD5;
            }
        }
        for(String sha1item :sha1List){//todo：这里需要修噶 保证在md5的情况下判断是否是sha1
            if( arg.equals(sha1item)){
                return algorithmType.AlgorithmSHA1;
            }
        }
        for(String crc32item :crc32List){
            if(arg.equals(crc32item)){
                return algorithmType.AlgorithmCRC32;
            }
        }
        return algorithmType.AlgorithmUNKNOWN;
    }

    public static String GetMemoryAddr(String inputMemory){
        String addressPattern = "0x[0-9a-fA-F]+";
        String address = inputMemory.matches(".*(" + addressPattern + ").*") ? inputMemory.replaceAll(".*(" + addressPattern + ").*", "$1") : null;
        return address;
    }
    public static String GetMemoryValue(String inputMemory){
        String[] parts = inputMemory.split(", ");
        String dataSizeStr = parts[1].split(" = ")[1]; // "data size = 1" -> "1"
        int dataSize = Integer.parseInt(dataSizeStr);
        if(dataSize==1){
            String dataValueStr = parts[2].split(" = ")[1]; // "data value = 63" -> "63"
            return dataValueStr;
        }
        return "";
    }


    /**
     * 分割字符串
     * @param inputString
     * @return
     */
    public static String[] splitStringByComma(String inputString) {
        return inputString.split(" ");
    }



    public static String LongToHex(Long num) {
        String hexWithoutPrefix = Long.toHexString(num);
        String hexWithPrefix = "0x" + hexWithoutPrefix;
        return hexWithPrefix;
    }

    public static long hexToLong(String hex) {
        if (hex == null || !hex.startsWith("0x") || hex.length() < 3) {
            throw new IllegalArgumentException("Invalid hex string format");
        }
        String hexValue = hex.substring(2);
        return Long.parseLong(hexValue, 16);
    }

    //AES start:
    public static List<AesSboxEntity> aesSboxMapList = new ArrayList();
    private static void removeList(int index){
        aesSboxMapList.remove(index);
    }
    //aes sbox下一次应该的地址
    public static String AESSboxNextMemory(String aesSboxaddr,int aesSboxindex){
        Long num = hexToLong(aesSboxaddr) + aesSboxindex;
        return LongToHex(num);
    }
    public static void isAes(String line,Map map){
        String datavalue = GetMemoryValue(line);
        if (!datavalue.equals("")){
            if (datavalue.equals(aesSbox[0])){
                AesSboxEntity aesSboxEntity = new AesSboxEntity();
                aesSboxEntity.setAesSboxaddr(GetMemoryAddr(line));
                aesSboxEntity.setAesSboxindex(1);
                aesSboxEntity.setAddrindex("");
                aesSboxEntity.setMap(map);
                aesSboxMapList.add(aesSboxEntity);
            }
            for (int i=0;i<aesSboxMapList.size();i++){
                AesSboxEntity aesSboxEntity = aesSboxMapList.get(i);
//                if ("0x7a449a78d0".equals(GetMemoryAddr(line))){
//                    System.out.println("debugger");
//                }
                if(!aesSboxEntity.getAesSboxaddr().equals("")){
//                    下一次应该出现的偏移复制
                    aesSboxEntity.setAddrindex(AESSboxNextMemory(aesSboxEntity.getAesSboxaddr(),aesSboxEntity.getAesSboxindex())) ;
                }
                if (aesSbox[aesSboxEntity.getAesSboxindex()].equals(datavalue)&&aesSboxEntity.getAddrindex().equals(GetMemoryAddr(line))){
                    System.out.println(aesSboxEntity.getAesSboxindex() +" : "+datavalue);
                    aesSboxEntity.setAesSboxindex(aesSboxEntity.getAesSboxindex()+1);
                    if(aesSboxEntity.getAesSboxindex()>=2){

                        boolean b1 = moreSbox(i,205);
                        boolean b2 = moreSbox(i,145);
                        boolean b3 = moreSbox(i,123);
                        boolean b4 = moreSbox(i,255);
                        if(b1 && b2 && b3 && b4){

                            SaveLog.storeContentByLine( "Aes encode :  sbox addr:" + AlgorithmAutoEmitterUtility.aesSboxMapList.get(i).getMap().get("deviation") +"  _ "+map.toString(),publicEntity.path+"/trace_algorithm.log");
                            SaveLog.storeContentByLine( "Aes encode :  sbox manmery:" + AlgorithmAutoEmitterUtility.aesSboxMapList.get(i).toString()+"\n",publicEntity.path+"/trace_algorithm.log");
                            removeList(i);
                        }
                    }
                }else if((!aesSbox[aesSboxEntity.getAesSboxindex()].equals(datavalue))&&aesSboxEntity.getAddrindex().equals(GetMemoryAddr(line))){
                    removeList(i);
                }
            }
        }
    }



public static boolean moreSbox(int index,int sboxindex){
    String addr = AlgorithmAutoEmitterUtility.AESSboxNextMemory(AlgorithmAutoEmitterUtility.aesSboxMapList.get(index).getAesSboxaddr(),sboxindex);
    String str = "memory read  at "+addr+".*"+AlgorithmAutoEmitterUtility.aesSbox[sboxindex];
    return searchInDirectory("/"+publicEntity.path+"/trace.log",str);
}
    public static boolean searchInDirectory(String dirPath, String keyword) {
        boolean foundAny = false;
        try {
            // 编译正则表达式
            Pattern pattern = Pattern.compile(keyword);

            // 获取目录下所有文本文件和日志文件
            Collection<File> files = FileUtils.listFiles(
                    new File(dirPath),
                    new String[]{"txt", "log"},
                    true
            );

            // 遍历所有文件
            for (File file : files) {
                boolean fileHasMatch = false;
                LineIterator it = null;
                try {
                    it = FileUtils.lineIterator(file, "UTF-8");

                    // 逐行检查匹配
                    while (it.hasNext()) {
                        String line = it.nextLine();
                        if (pattern.matcher(line).find()) {
                            if (!fileHasMatch) {
                                System.out.println("\nFound in file: " + file.getAbsolutePath());
                                fileHasMatch = true;
                                foundAny = true;
                            }
                            System.out.println("  ▶ " + line);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    LineIterator.closeQuietly(it);
                }
            }
        } catch (PatternSyntaxException e) {
            System.err.println("[Error] Invalid regular expression: " + keyword);
            e.printStackTrace();
            return false;
        }
        return foundAny;
    }


}
