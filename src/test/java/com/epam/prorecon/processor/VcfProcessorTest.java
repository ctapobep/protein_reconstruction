package com.epam.prorecon.processor;

import com.epam.prorecon.FileReaderUtils;
import htsjdk.samtools.util.CloserUtil;
import htsjdk.tribble.index.Index;
import htsjdk.tribble.index.IndexFactory;
import htsjdk.tribble.util.LittleEndianOutputStream;
import htsjdk.variant.vcf.VCFCodec;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;

public class VcfProcessorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(VcfProcessorTest.class);

    @Test
    public void nucliotideStringAfterApplyingVcfShouldBeEqualToPrecalculatedConstant() throws IOException, CloneNotSupportedException {
        String fastaFileSubSequence = FileReaderUtils.readSequenceFromFastaFile(
                this.getClass().getClassLoader().getResource("dmel-all-chromosome-r606.fasta"/*"Ref1.fasta"*/)
                        .getPath(), "X", 12584385, 12592193);

        URL vcfFileUrl = this.getClass().getClassLoader().getResource("agnX1.model.2.snp-indels.vcf"/*"mutatsii.vcf"*/);
        Assert.assertNotNull(vcfFileUrl);
        File vcfFile = new File(vcfFileUrl.getPath());

        File vcfIndexFile = new File(vcfFileUrl.getPath() + ".Idx");
        Index idx = IndexFactory.createIndex(vcfFile, new VCFCodec(), IndexFactory.IndexType.LINEAR);

        LittleEndianOutputStream stream = null;
        stream = new LittleEndianOutputStream(new BufferedOutputStream(new FileOutputStream(vcfIndexFile)));
        idx.write(stream);
        stream.close();

        CloserUtil.close(vcfFile);
        CloserUtil.close(vcfIndexFile);

        VcfProcessor vcfProcessor = new VcfProcessor(FileReaderUtils.readVariantContextsFromVcfFile(
                vcfFileUrl.getPath(), "X", 12584385, 12592193));

        vcfProcessor.process(fastaFileSubSequence, fastaFileSubSequence, 12584385, 12584385);
        for (String currString : vcfProcessor.getPossibleFinalStrings()) {
            LOGGER.warn(currString);
        }
//        LOGGER.warn(fastaFileSubSequence);
    }
}
