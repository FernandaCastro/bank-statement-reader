package com.example.bank;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

@Service
public class ExtractService {

    @Value("${extract.category.rio}")
    private String rio;
    @Value("${extract.category.saquarema}")
    private String saquarema;
    @Value("${extract.category.mercado}")
    private String mercado;
    @Value("${extract.category.pessoal}")
    private String pessoal;
    @Value("${extract.category.cartao}")
    private String cartao;

    public List<Extract> read(MultipartFile file) throws Exception {

        // parse CSV file to create a list of `User` objects
        Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "ISO-8859-1"));

        // create csv bean reader
        CsvToBean<Extract> csvToBean = new CsvToBeanBuilder<Extract>(reader)
                .withType(Extract.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        // convert `CsvToBean` object to list of users
        return csvToBean.parse();
    }

    public double categorizeRio(List<Extract> extracts){
        List<String> rioProperties = Arrays.asList(rio.trim().split(",").clone());

        return extracts.stream()
                .filter(p -> this.contains(p.getHistorico(), rioProperties))
                .mapToDouble(Extract::getValor).sum();
    }

    public double categorizeSaquarema(List<Extract> extracts){
        List<String> saquaremaProperties = Arrays.asList(saquarema.trim().split(",").clone());

        return extracts.stream()
                .filter(p -> this.contains(p.getHistorico(), saquaremaProperties))
                .mapToDouble(Extract::getValor).sum();
    }

    public double categorizeMercado(List<Extract> extracts){
        List<String> mercadoProperties = Arrays.asList(mercado.trim().split(",").clone());

        return extracts.stream()
                .filter(p -> this.contains(p.getHistorico(), mercadoProperties))
                .mapToDouble(Extract::getValor).sum();
    }

    public double categorizePessoal(List<Extract> extracts){
        List<String> pessoalProperties = Arrays.asList(pessoal.trim().split(",").clone());
        List<String> mercadoProperties = Arrays.asList(mercado.trim().split(",").clone());

        return extracts.stream()
                .filter(p -> this.contains(p.getHistorico(), pessoalProperties))
                .filter(p -> !this.contains(p.getHistorico(), mercadoProperties))
                .mapToDouble(Extract::getValor).sum();
    }

    public double categorizeCartao(List<Extract> extracts){
        List<String> cartaoProperties = Arrays.asList(cartao.trim().split(",").clone());

        return extracts.stream()
                .filter(p -> this.contains(p.getHistorico(), cartaoProperties))
                .mapToDouble(Extract::getValor).sum();
    }

    public boolean contains(String historico, List<String> categoria){
        for(String prop : categoria) {
            if (historico.toUpperCase().contains(prop.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}
