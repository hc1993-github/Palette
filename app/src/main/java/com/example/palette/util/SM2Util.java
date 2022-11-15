package com.example.palette.util;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithID;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.custom.gm.SM2P256V1Curve;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECFieldFp;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SM2Util {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static final SM2P256V1Curve CURVE = new SM2P256V1Curve();
    public final static BigInteger SM2_ECC_P = CURVE.getQ();
    public final static BigInteger SM2_ECC_A = CURVE.getA().toBigInteger();
    public final static BigInteger SM2_ECC_B = CURVE.getB().toBigInteger();
    public final static BigInteger SM2_ECC_N = CURVE.getOrder();
    public final static BigInteger SM2_ECC_H = CURVE.getCofactor();
    public final static BigInteger SM2_ECC_GX = new BigInteger(
            "32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7", 16);
    public final static BigInteger SM2_ECC_GY = new BigInteger(
            "BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0", 16);
    public static final ECPoint G_POINT = CURVE.createPoint(SM2_ECC_GX, SM2_ECC_GY);
    public static final ECDomainParameters DOMAIN_PARAMS = new ECDomainParameters(CURVE, G_POINT,
            SM2_ECC_N, SM2_ECC_H);
    public static final int CURVE_LEN = BCECUtil.getCurveLength(DOMAIN_PARAMS);
    //////////////////////////////////////////////////////////////////////////////////////

    public static final EllipticCurve JDK_CURVE = new EllipticCurve(new ECFieldFp(SM2_ECC_P), SM2_ECC_A, SM2_ECC_B);
    public static final java.security.spec.ECPoint JDK_G_POINT = new java.security.spec.ECPoint(
            G_POINT.getAffineXCoord().toBigInteger(), G_POINT.getAffineYCoord().toBigInteger());
    public static final java.security.spec.ECParameterSpec JDK_EC_SPEC = new java.security.spec.ECParameterSpec(
            JDK_CURVE, JDK_G_POINT, SM2_ECC_N, SM2_ECC_H.intValue());

    //////////////////////////////////////////////////////////////////////////////////////

    public static final int SM3_DIGEST_LENGTH = 32;

    private static X9ECParameters x9ECParameters = GMNamedCurves.getByName("sm2p256v1");
    private static ECParameterSpec ecParameterSpec = new ECParameterSpec(x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN());


    /**
     * 生成ECC密钥对
     *
     * @return ECC密钥对
     */
    private static AsymmetricCipherKeyPair generateKeyPairParameter() {
        SecureRandom random = new SecureRandom();
        return BCECUtil.generateKeyPairParameter(DOMAIN_PARAMS, random);
    }

    public static KeyPair generateKeyPair() throws NoSuchProviderException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException {
        SecureRandom random = new SecureRandom();
        return BCECUtil.generateKeyPair(DOMAIN_PARAMS, random);
    }

    public static String getPublicKeyHex(PublicKey k) {
        return ByteUtils.toHexString((((BCECPublicKey) k).getQ().getEncoded(false)));
    }

    public static String getPrivateKeyHex(PrivateKey k) {
        return ByteUtils.toHexString((((BCECPrivateKey) k).getD().toByteArray()));
    }

    private static BCECPublicKey makePublicKey(String hex) {
        byte[] keyBytes = ByteUtils.fromHexString(hex);
        ECPoint p = DOMAIN_PARAMS.getCurve().decodePoint(keyBytes);
        ECPublicKeySpec kSpec = new ECPublicKeySpec(p, ecParameterSpec);
        return new BCECPublicKey("EC", kSpec, BouncyCastleProvider.CONFIGURATION);

    }

    private static byte[] encrypt(BCECPublicKey pubKey, byte[] srcData) throws InvalidCipherTextException {
        ECPublicKeyParameters pubKeyParameters = BCECUtil.convertPublicKeyToParameters(pubKey);
        return encrypt(pubKeyParameters, srcData);
    }

    /**
     * ECC公钥加密
     *
     * @param pubKeyParameters ECC公钥
     * @param srcData          源数据
     * @return SM2密文，实际包含三部分：ECC公钥、真正的密文、公钥和原文的SM3-HASH值
     * @throws InvalidCipherTextException
     */
    private static byte[] encrypt(ECPublicKeyParameters pubKeyParameters, byte[] srcData)
            throws InvalidCipherTextException {
        SM2Engine engine = new SM2Engine();
        ParametersWithRandom pwr = new ParametersWithRandom(pubKeyParameters, new SecureRandom());
        engine.init(true, pwr);
        return engine.processBlock(srcData, 0, srcData.length);
    }

    public static String encryptString(String raw, String publicKey) {
        BCECPublicKey k = makePublicKey(publicKey);
        try {
            return ByteUtils.toHexString(encrypt(k, raw.getBytes()));
        } catch (InvalidCipherTextException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] decrypt(BCECPrivateKey priKey, byte[] sm2Cipher) throws InvalidCipherTextException {
        ECPrivateKeyParameters priKeyParameters = BCECUtil.convertPrivateKeyToParameters(priKey);
        return decrypt(priKeyParameters, sm2Cipher);
    }

    /**
     * ECC私钥解密
     *
     * @param priKeyParameters ECC私钥
     * @param sm2Cipher        SM2密文，实际包含三部分：ECC公钥、真正的密文、公钥和原文的SM3-HASH值
     * @return 原文
     * @throws InvalidCipherTextException
     */
    private static byte[] decrypt(ECPrivateKeyParameters priKeyParameters, byte[] sm2Cipher)
            throws InvalidCipherTextException {
        SM2Engine engine = new SM2Engine();
        engine.init(false, priKeyParameters);
        return engine.processBlock(sm2Cipher, 0, sm2Cipher.length);
    }

    public static String decryptString(String cipher, String privateKey) {
        ECPrivateKeyParameters k = new ECPrivateKeyParameters(new BigInteger(ByteUtils.fromHexString(privateKey)), DOMAIN_PARAMS);
        try {
            return new String(decrypt(k, ByteUtils.fromHexString(cipher)));
        } catch (InvalidCipherTextException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 分解SM2密文
     *
     * @param cipherText SM2密文
     * @return
     */
    public static SM2Cipher parseSM2Cipher(byte[] cipherText) {
        int curveLength = BCECUtil.getCurveLength(DOMAIN_PARAMS);
        return parseSM2Cipher(curveLength, SM3_DIGEST_LENGTH, cipherText);
    }

    /**
     * 分解SM2密文
     *
     * @param curveLength  ECC曲线长度
     * @param digestLength HASH长度
     * @param cipherText   SM2密文
     * @return
     */
    public static SM2Cipher parseSM2Cipher(int curveLength, int digestLength,
                                           byte[] cipherText) {
        byte[] c1 = new byte[curveLength * 2 + 1];
        System.arraycopy(cipherText, 0, c1, 0, c1.length);
        byte[] c2 = new byte[cipherText.length - c1.length - digestLength];
        System.arraycopy(cipherText, c1.length, c2, 0, c2.length);
        byte[] c3 = new byte[digestLength];
        System.arraycopy(cipherText, c1.length + c2.length, c3, 0, c3.length);
        SM2Cipher result = new SM2Cipher();
        result.setC1(c1);
        result.setC2(c2);
        result.setC3(c3);
        result.setCipherText(cipherText);
        return result;
    }

    /**
     * DER编码C1C2C3密文（根据《SM2密码算法使用规范》 GM/T 0009-2012）
     *
     * @param cipher
     * @return
     * @throws IOException
     */
    public static byte[] encodeSM2CipherToDER(byte[] cipher) throws IOException {
        int curveLength = BCECUtil.getCurveLength(DOMAIN_PARAMS);
        return encodeSM2CipherToDER(curveLength, SM3_DIGEST_LENGTH, cipher);
    }

    /**
     * DER编码C1C2C3密文（根据《SM2密码算法使用规范》 GM/T 0009-2012）
     *
     * @param curveLength
     * @param digestLength
     * @param cipher
     * @return
     * @throws IOException
     */
    public static byte[] encodeSM2CipherToDER(int curveLength, int digestLength, byte[] cipher)
            throws IOException {
        int startPos = 1;

        byte[] c1x = new byte[curveLength];
        System.arraycopy(cipher, startPos, c1x, 0, c1x.length);
        startPos += c1x.length;

        byte[] c1y = new byte[curveLength];
        System.arraycopy(cipher, startPos, c1y, 0, c1y.length);
        startPos += c1y.length;

        byte[] c2 = new byte[cipher.length - c1x.length - c1y.length - 1 - digestLength];
        System.arraycopy(cipher, startPos, c2, 0, c2.length);
        startPos += c2.length;

        byte[] c3 = new byte[digestLength];
        System.arraycopy(cipher, startPos, c3, 0, c3.length);

        ASN1Encodable[] arr = new ASN1Encodable[4];
        arr[0] = new ASN1Integer(c1x);
        arr[1] = new ASN1Integer(c1y);
        arr[2] = new DEROctetString(c3);
        arr[3] = new DEROctetString(c2);
        DERSequence ds = new DERSequence(arr);
        return ds.getEncoded(ASN1Encoding.DER);
    }

    /**
     * 解DER编码密文（根据《SM2密码算法使用规范》 GM/T 0009-2012）
     *
     * @param derCipher
     * @return
     */
    public static byte[] decodeDERSM2Cipher(byte[] derCipher) {
        ASN1Sequence as = DERSequence.getInstance(derCipher);
        byte[] c1x = ((ASN1Integer) as.getObjectAt(0)).getValue().toByteArray();
        byte[] c1y = ((ASN1Integer) as.getObjectAt(1)).getValue().toByteArray();
        byte[] c3 = ((DEROctetString) as.getObjectAt(2)).getOctets();
        byte[] c2 = ((DEROctetString) as.getObjectAt(3)).getOctets();

        int pos = 0;
        byte[] cipherText = new byte[1 + c1x.length + c1y.length + c2.length + c3.length];

        final byte uncompressedFlag = 0x04;
        cipherText[0] = uncompressedFlag;
        pos += 1;

        System.arraycopy(c1x, 0, cipherText, pos, c1x.length);
        pos += c1x.length;

        System.arraycopy(c1y, 0, cipherText, pos, c1y.length);
        pos += c1y.length;

        System.arraycopy(c2, 0, cipherText, pos, c2.length);
        pos += c2.length;

        System.arraycopy(c3, 0, cipherText, pos, c3.length);

        return cipherText;
    }

    public static byte[] sign(BCECPrivateKey priKey, byte[] srcData) throws NoSuchAlgorithmException,
            NoSuchProviderException, CryptoException {
        ECPrivateKeyParameters priKeyParameters = BCECUtil.convertPrivateKeyToParameters(priKey);
        return sign(priKeyParameters, null, srcData);
    }

    /**
     * ECC私钥签名
     * 不指定withId，则默认withId为字节数组:"1234567812345678".getBytes()
     *
     * @param priKeyParameters ECC私钥
     * @param srcData          源数据
     * @return 签名
     * @throws CryptoException
     */
    public static byte[] sign(ECPrivateKeyParameters priKeyParameters, byte[] srcData) throws CryptoException {
        return sign(priKeyParameters, null, srcData);
    }

    public static byte[] sign(BCECPrivateKey priKey, byte[] withId, byte[] srcData) throws CryptoException {
        ECPrivateKeyParameters priKeyParameters = BCECUtil.convertPrivateKeyToParameters(priKey);
        return sign(priKeyParameters, withId, srcData);
    }

    /**
     * ECC私钥签名
     *
     * @param priKeyParameters ECC私钥
     * @param withId           可以为null，若为null，则默认withId为字节数组:"1234567812345678".getBytes()
     * @param srcData          源数据
     * @return 签名
     * @throws CryptoException
     */
    public static byte[] sign(ECPrivateKeyParameters priKeyParameters, byte[] withId, byte[] srcData)
            throws CryptoException {
        SM2Signer signer = new SM2Signer();
        CipherParameters param = null;
        ParametersWithRandom pwr = new ParametersWithRandom(priKeyParameters, new SecureRandom());
        if (withId != null) {
            param = new ParametersWithID(pwr, withId);
        } else {
            param = pwr;
        }
        signer.init(true, param);
        signer.update(srcData, 0, srcData.length);
        return signer.generateSignature();
    }

    /**
     * 将DER编码的SM2签名解析成64字节的纯R+S字节流
     *
     * @param derSign
     * @return
     */
    public static byte[] decodeDERSM2Sign(byte[] derSign) {
        ASN1Sequence as = DERSequence.getInstance(derSign);
        byte[] rBytes = ((ASN1Integer) as.getObjectAt(0)).getValue().toByteArray();
        byte[] sBytes = ((ASN1Integer) as.getObjectAt(1)).getValue().toByteArray();
        //由于大数的补0规则，所以可能会出现33个字节的情况，要修正回32个字节
        rBytes = fixToCurveLengthBytes(rBytes);
        sBytes = fixToCurveLengthBytes(sBytes);
        byte[] rawSign = new byte[rBytes.length + sBytes.length];
        System.arraycopy(rBytes, 0, rawSign, 0, rBytes.length);
        System.arraycopy(sBytes, 0, rawSign, rBytes.length, sBytes.length);
        return rawSign;
    }

    /**
     * 把64字节的纯R+S字节流转换成DER编码字节流
     *
     * @param rawSign
     * @return
     * @throws IOException
     */
    public static byte[] encodeSM2SignToDER(byte[] rawSign) throws IOException {
        //要保证大数是正数
        BigInteger r = new BigInteger(1, extractBytes(rawSign, 0, 32));
        BigInteger s = new BigInteger(1, extractBytes(rawSign, 32, 32));
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(r));
        v.add(new ASN1Integer(s));
        return new DERSequence(v).getEncoded(ASN1Encoding.DER);
    }

    public static boolean verify(BCECPublicKey pubKey, byte[] srcData, byte[] sign) {
        ECPublicKeyParameters pubKeyParameters = BCECUtil.convertPublicKeyToParameters(pubKey);
        return verify(pubKeyParameters, null, srcData, sign);
    }

    /**
     * ECC公钥验签
     * 不指定withId，则默认withId为字节数组:"1234567812345678".getBytes()
     *
     * @param pubKeyParameters ECC公钥
     * @param srcData          源数据
     * @param sign             签名
     * @return 验签成功返回true，失败返回false
     */
    public static boolean verify(ECPublicKeyParameters pubKeyParameters, byte[] srcData, byte[] sign) {
        return verify(pubKeyParameters, null, srcData, sign);
    }

    public static boolean verify(BCECPublicKey pubKey, byte[] withId, byte[] srcData, byte[] sign) {
        ECPublicKeyParameters pubKeyParameters = BCECUtil.convertPublicKeyToParameters(pubKey);
        return verify(pubKeyParameters, withId, srcData, sign);
    }

    /**
     * ECC公钥验签
     *
     * @param pubKeyParameters ECC公钥
     * @param withId           可以为null，若为null，则默认withId为字节数组:"1234567812345678".getBytes()
     * @param srcData          源数据
     * @param sign             签名
     * @return 验签成功返回true，失败返回false
     */
    public static boolean verify(ECPublicKeyParameters pubKeyParameters, byte[] withId, byte[] srcData, byte[] sign) {
        SM2Signer signer = new SM2Signer();
        CipherParameters param;
        if (withId != null) {
            param = new ParametersWithID(pubKeyParameters, withId);
        } else {
            param = pubKeyParameters;
        }
        signer.init(false, param);
        signer.update(srcData, 0, srcData.length);
        return signer.verifySignature(sign);
    }

    private static byte[] extractBytes(byte[] src, int offset, int length) {
        byte[] result = new byte[length];
        System.arraycopy(src, offset, result, 0, result.length);
        return result;
    }

    private static byte[] fixToCurveLengthBytes(byte[] src) {
        if (src.length == CURVE_LEN) {
            return src;
        }

        byte[] result = new byte[CURVE_LEN];
        if (src.length > CURVE_LEN) {
            System.arraycopy(src, src.length - result.length, result, 0, result.length);
        } else {
            System.arraycopy(src, 0, result, result.length - src.length, src.length);
        }
        return result;
    }

    private static class SM2Cipher {
        /**
         * ECC密钥
         */
        private byte[] c1;

        /**
         * 真正的密文
         */
        private byte[] c2;

        /**
         * 对（c1+c2）的SM3-HASH值
         */
        private byte[] c3;

        /**
         * SM2标准的密文，即（c1+c2+c3）
         */
        private byte[] cipherText;

        public byte[] getC1() {
            return c1;
        }

        public void setC1(byte[] c1) {
            this.c1 = c1;
        }

        public byte[] getC2() {
            return c2;
        }

        public void setC2(byte[] c2) {
            this.c2 = c2;
        }

        public byte[] getC3() {
            return c3;
        }

        public void setC3(byte[] c3) {
            this.c3 = c3;
        }

        public byte[] getCipherText() {
            return cipherText;
        }

        public void setCipherText(byte[] cipherText) {
            this.cipherText = cipherText;
        }
    }

    private static class BCECUtil {
        private static final String ALGO_NAME_EC = "EC";
        private static final String PEM_STRING_PUBLIC = "PUBLIC KEY";
        private static final String PEM_STRING_ECPRIVATEKEY = "EC PRIVATE KEY";

        /**
         * 生成ECC密钥对
         *
         * @return ECC密钥对
         */
        public static AsymmetricCipherKeyPair generateKeyPairParameter(ECDomainParameters domainParameters,
                                                                       SecureRandom random) {
            ECKeyGenerationParameters keyGenerationParams = new ECKeyGenerationParameters(domainParameters,
                    random);
            ECKeyPairGenerator keyGen = new ECKeyPairGenerator();
            keyGen.init(keyGenerationParams);
            return keyGen.generateKeyPair();
        }

        public static KeyPair generateKeyPair(ECDomainParameters domainParameters, SecureRandom random)
                throws NoSuchProviderException, NoSuchAlgorithmException,
                InvalidAlgorithmParameterException {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGO_NAME_EC, BouncyCastleProvider.PROVIDER_NAME);
            ECParameterSpec parameterSpec = new ECParameterSpec(domainParameters.getCurve(), domainParameters.getG(),
                    domainParameters.getN(), domainParameters.getH(), domainParameters.getSeed());
            kpg.initialize(parameterSpec, random);
            return kpg.generateKeyPair();
        }

        public static int getCurveLength(ECKeyParameters ecKey) {
            return getCurveLength(ecKey.getParameters());
        }

        public static int getCurveLength(ECDomainParameters domainParams) {
            return (domainParams.getCurve().getFieldSize() + 7) / 8;
        }

        public static byte[] fixToCurveLengthBytes(int curveLength, byte[] src) {
            if (src.length == curveLength) {
                return src;
            }

            byte[] result = new byte[curveLength];
            if (src.length > curveLength) {
                System.arraycopy(src, src.length - result.length, result, 0, result.length);
            } else {
                System.arraycopy(src, 0, result, result.length - src.length, src.length);
            }
            return result;
        }

        public static ECPrivateKeyParameters createECPrivateKeyParameters(BigInteger d,
                                                                          ECDomainParameters domainParameters) {
            return new ECPrivateKeyParameters(d, domainParameters);
        }

        public static ECPublicKeyParameters createECPublicKeyParameters(BigInteger x, BigInteger y,
                                                                        ECCurve curve, ECDomainParameters domainParameters) {
            return createECPublicKeyParameters(x.toByteArray(), y.toByteArray(), curve, domainParameters);
        }

        public static ECPublicKeyParameters createECPublicKeyParameters(String xHex, String yHex,
                                                                        ECCurve curve, ECDomainParameters domainParameters) {
            return createECPublicKeyParameters(ByteUtils.fromHexString(xHex), ByteUtils.fromHexString(yHex),
                    curve, domainParameters);
        }

        public static ECPublicKeyParameters createECPublicKeyParameters(byte[] xBytes, byte[] yBytes,
                                                                        ECCurve curve, ECDomainParameters domainParameters) {
            final byte uncompressedFlag = 0x04;
            int curveLength = getCurveLength(domainParameters);
            xBytes = fixToCurveLengthBytes(curveLength, xBytes);
            yBytes = fixToCurveLengthBytes(curveLength, yBytes);
            byte[] encodedPubKey = new byte[1 + xBytes.length + yBytes.length];
            encodedPubKey[0] = uncompressedFlag;
            System.arraycopy(xBytes, 0, encodedPubKey, 1, xBytes.length);
            System.arraycopy(yBytes, 0, encodedPubKey, 1 + xBytes.length, yBytes.length);
            return new ECPublicKeyParameters(curve.decodePoint(encodedPubKey), domainParameters);
        }

        public static ECPrivateKeyParameters convertPrivateKeyToParameters(BCECPrivateKey ecPriKey) {
            ECParameterSpec parameterSpec = ecPriKey.getParameters();
            ECDomainParameters domainParameters = new ECDomainParameters(parameterSpec.getCurve(), parameterSpec.getG(),
                    parameterSpec.getN(), parameterSpec.getH());
            return new ECPrivateKeyParameters(ecPriKey.getD(), domainParameters);
        }

        public static ECPublicKeyParameters convertPublicKeyToParameters(BCECPublicKey ecPubKey) {
            ECParameterSpec parameterSpec = ecPubKey.getParameters();
            ECDomainParameters domainParameters = new ECDomainParameters(parameterSpec.getCurve(), parameterSpec.getG(),
                    parameterSpec.getN(), parameterSpec.getH());
            return new ECPublicKeyParameters(ecPubKey.getQ(), domainParameters);
        }

        public static BCECPublicKey createPublicKeyFromSubjectPublicKeyInfo(SubjectPublicKeyInfo subPubInfo) throws NoSuchProviderException,
                NoSuchAlgorithmException, InvalidKeySpecException, IOException {
            return BCECUtil.convertX509ToECPublicKey(subPubInfo.toASN1Primitive().getEncoded(ASN1Encoding.DER));
        }

        /**
         * 将ECC私钥转换为PKCS8标准的字节流
         *
         * @param priKey
         * @param pubKey 可以为空，但是如果为空的话得到的结果OpenSSL可能解析不了
         * @return
         */
        public static byte[] convertECPrivateKeyToPKCS8(ECPrivateKeyParameters priKey,
                                                        ECPublicKeyParameters pubKey) {
            ECDomainParameters domainParams = priKey.getParameters();
            ECParameterSpec spec = new ECParameterSpec(domainParams.getCurve(), domainParams.getG(),
                    domainParams.getN(), domainParams.getH());
            BCECPublicKey publicKey = null;
            if (pubKey != null) {
                publicKey = new BCECPublicKey(ALGO_NAME_EC, pubKey, spec,
                        BouncyCastleProvider.CONFIGURATION);
            }
            BCECPrivateKey privateKey = new BCECPrivateKey(ALGO_NAME_EC, priKey, publicKey,
                    spec, BouncyCastleProvider.CONFIGURATION);
            return privateKey.getEncoded();
        }

        /**
         * 将PKCS8标准的私钥字节流转换为私钥对象
         *
         * @param pkcs8Key
         * @return
         * @throws NoSuchAlgorithmException
         * @throws NoSuchProviderException
         * @throws InvalidKeySpecException
         */
        public static BCECPrivateKey convertPKCS8ToECPrivateKey(byte[] pkcs8Key)
                throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
            PKCS8EncodedKeySpec peks = new PKCS8EncodedKeySpec(pkcs8Key);
            KeyFactory kf = KeyFactory.getInstance(ALGO_NAME_EC, BouncyCastleProvider.PROVIDER_NAME);
            return (BCECPrivateKey) kf.generatePrivate(peks);
        }

        /**
         * 将PKCS8标准的私钥字节流转换为PEM
         *
         * @param encodedKey
         * @return
         * @throws IOException
         */
        public static String convertECPrivateKeyPKCS8ToPEM(byte[] encodedKey) throws IOException {
            return convertEncodedDataToPEM(PEM_STRING_ECPRIVATEKEY, encodedKey);
        }

        /**
         * 将PEM格式的私钥转换为PKCS8标准字节流
         *
         * @param pemString
         * @return
         * @throws IOException
         */
        public static byte[] convertECPrivateKeyPEMToPKCS8(String pemString) throws IOException {
            return convertPEMToEncodedData(pemString);
        }

        /**
         * 将ECC私钥转换为SEC1标准的字节流
         * openssl d2i_ECPrivateKey函数要求的DER编码的私钥也是SEC1标准的，
         * 这个工具函数的主要目的就是为了能生成一个openssl可以直接“识别”的ECC私钥.
         * 相对RSA私钥的PKCS1标准，ECC私钥的标准为SEC1
         *
         * @param priKey
         * @param pubKey
         * @return
         * @throws IOException
         */
        public static byte[] convertECPrivateKeyToSEC1(ECPrivateKeyParameters priKey,
                                                       ECPublicKeyParameters pubKey) throws IOException {
            byte[] pkcs8Bytes = convertECPrivateKeyToPKCS8(priKey, pubKey);
            PrivateKeyInfo pki = PrivateKeyInfo.getInstance(pkcs8Bytes);
            ASN1Encodable encodable = pki.parsePrivateKey();
            ASN1Primitive primitive = encodable.toASN1Primitive();
            byte[] sec1Bytes = primitive.getEncoded();
            return sec1Bytes;
        }

        /**
         * 将SEC1标准的私钥字节流恢复为PKCS8标准的字节流
         *
         * @param sec1Key
         * @return
         * @throws IOException
         */
        public static byte[] convertECPrivateKeySEC1ToPKCS8(byte[] sec1Key) throws IOException {
            /**
             * 参考org.bouncycastle.asn1.pkcs.PrivateKeyInfo和
             * org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey，逆向拼装
             */
            X962Parameters params = getDomainParametersFromName(SM2Util.JDK_EC_SPEC, false);
            ASN1OctetString privKey = new DEROctetString(sec1Key);
            ASN1EncodableVector v = new ASN1EncodableVector();
            v.add(new ASN1Integer(0)); //版本号
            v.add(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, params)); //算法标识
            v.add(privKey);
            DERSequence ds = new DERSequence(v);
            return ds.getEncoded(ASN1Encoding.DER);
        }

        /**
         * 将SEC1标准的私钥字节流转为BCECPrivateKey对象
         *
         * @param sec1Key
         * @return
         * @throws NoSuchAlgorithmException
         * @throws NoSuchProviderException
         * @throws InvalidKeySpecException
         * @throws IOException
         */
        public static BCECPrivateKey convertSEC1ToBCECPrivateKey(byte[] sec1Key)
                throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, IOException {
            PKCS8EncodedKeySpec peks = new PKCS8EncodedKeySpec(convertECPrivateKeySEC1ToPKCS8(sec1Key));
            KeyFactory kf = KeyFactory.getInstance(ALGO_NAME_EC, BouncyCastleProvider.PROVIDER_NAME);
            return (BCECPrivateKey) kf.generatePrivate(peks);
        }

        /**
         * 将SEC1标准的私钥字节流转为ECPrivateKeyParameters对象
         * openssl i2d_ECPrivateKey函数生成的DER编码的ecc私钥是：SEC1标准的、带有EC_GROUP、带有公钥的，
         * 这个工具函数的主要目的就是为了使Java程序能够“识别”openssl生成的ECC私钥
         *
         * @param sec1Key
         * @return
         * @throws NoSuchAlgorithmException
         * @throws NoSuchProviderException
         * @throws InvalidKeySpecException
         */
        public static ECPrivateKeyParameters convertSEC1ToECPrivateKey(byte[] sec1Key)
                throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, IOException {
            BCECPrivateKey privateKey = convertSEC1ToBCECPrivateKey(sec1Key);
            return convertPrivateKeyToParameters(privateKey);
        }

        /**
         * 将ECC公钥对象转换为X509标准的字节流
         *
         * @param pubKey
         * @return
         */
        public static byte[] convertECPublicKeyToX509(ECPublicKeyParameters pubKey) {
            ECDomainParameters domainParams = pubKey.getParameters();
            ECParameterSpec spec = new ECParameterSpec(domainParams.getCurve(), domainParams.getG(),
                    domainParams.getN(), domainParams.getH());
            BCECPublicKey publicKey = new BCECPublicKey(ALGO_NAME_EC, pubKey, spec,
                    BouncyCastleProvider.CONFIGURATION);
            return publicKey.getEncoded();
        }

        /**
         * 将X509标准的公钥字节流转为公钥对象
         *
         * @param x509Bytes
         * @return
         * @throws NoSuchProviderException
         * @throws NoSuchAlgorithmException
         * @throws InvalidKeySpecException
         */
        public static BCECPublicKey convertX509ToECPublicKey(byte[] x509Bytes) throws NoSuchProviderException,
                NoSuchAlgorithmException, InvalidKeySpecException {
            X509EncodedKeySpec eks = new X509EncodedKeySpec(x509Bytes);
            KeyFactory kf = KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
            return (BCECPublicKey) kf.generatePublic(eks);
        }

        /**
         * 将X509标准的公钥字节流转为PEM
         *
         * @param encodedKey
         * @return
         * @throws IOException
         */
        public static String convertECPublicKeyX509ToPEM(byte[] encodedKey) throws IOException {
            return convertEncodedDataToPEM(PEM_STRING_PUBLIC, encodedKey);
        }

        /**
         * 将PEM格式的公钥转为X509标准的字节流
         *
         * @param pemString
         * @return
         * @throws IOException
         */
        public static byte[] convertECPublicKeyPEMToX509(String pemString) throws IOException {
            return convertPEMToEncodedData(pemString);
        }


        public static X9ECParameters getDomainParametersFromGenSpec(ECGenParameterSpec genSpec) {
            return getDomainParametersFromName(genSpec.getName());
        }

        public static X9ECParameters getDomainParametersFromName(String curveName) {
            X9ECParameters domainParameters;
            try {
                if (curveName.charAt(0) >= '0' && curveName.charAt(0) <= '2') {
                    ASN1ObjectIdentifier oidID = new ASN1ObjectIdentifier(curveName);
                    domainParameters = ECUtil.getNamedCurveByOid(oidID);
                } else {
                    if (curveName.indexOf(' ') > 0) {
                        curveName = curveName.substring(curveName.indexOf(' ') + 1);
                        domainParameters = ECUtil.getNamedCurveByName(curveName);
                    } else {
                        domainParameters = ECUtil.getNamedCurveByName(curveName);
                    }
                }
            } catch (IllegalArgumentException ex) {
                domainParameters = ECUtil.getNamedCurveByName(curveName);
            }
            return domainParameters;
        }

        public static X962Parameters getDomainParametersFromName(java.security.spec.ECParameterSpec ecSpec,
                                                                 boolean withCompression) {
            X962Parameters params;

            if (ecSpec instanceof ECNamedCurveSpec) {
                ASN1ObjectIdentifier curveOid = ECUtil.getNamedCurveOid(((ECNamedCurveSpec) ecSpec).getName());
                if (curveOid == null) {
                    curveOid = new ASN1ObjectIdentifier(((ECNamedCurveSpec) ecSpec).getName());
                }
                params = new X962Parameters(curveOid);
            } else if (ecSpec == null) {
                params = new X962Parameters(DERNull.INSTANCE);
            } else {
                ECCurve curve = EC5Util.convertCurve(ecSpec.getCurve());

                X9ECParameters ecP = null;

                params = new X962Parameters(ecP);
            }

            return params;
        }

        private static String convertEncodedDataToPEM(String type, byte[] encodedData) throws IOException {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            PemWriter pWrt = new PemWriter(new OutputStreamWriter(bOut));
            try {
                PemObject pemObj = new PemObject(type, encodedData);
                pWrt.writeObject(pemObj);
            } finally {
                pWrt.close();
            }
            return new String(bOut.toByteArray());
        }

        private static byte[] convertPEMToEncodedData(String pemString) throws IOException {
            ByteArrayInputStream bIn = new ByteArrayInputStream(pemString.getBytes());
            PemReader pRdr = new PemReader(new InputStreamReader(bIn));
            try {
                PemObject pemObject = pRdr.readPemObject();
                return pemObject.getContent();
            } finally {
                pRdr.close();
            }
        }
    }
}
