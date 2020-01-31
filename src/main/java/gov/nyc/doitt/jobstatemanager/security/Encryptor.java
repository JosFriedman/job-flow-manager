package gov.nyc.doitt.jobstatemanager.security;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Encryptor {

	@Autowired
	private StandardPBEStringEncryptor standardPBEStringEncryptor;

	public String encrypt(String s) {
		return standardPBEStringEncryptor.encrypt(s);
	}

	public String decrypt(String s) {
		return standardPBEStringEncryptor.decrypt(s);
	}

}
