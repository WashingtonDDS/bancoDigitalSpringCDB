package br.com.cdb.bancoDigitalCdb.dto;

import lombok.Builder;

@Builder
public record MailBodyDTO(String to, String subject, String text) {

}
