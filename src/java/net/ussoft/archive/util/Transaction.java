package net.ussoft.archive.util;

import java.sql.Connection;

/**
 * ���������
 */
public class Transaction {
	//��ݿ����Ӷ���
	private Connection connection;
	//�������
	private int transCount;
	//�ύ����
	private int commitCount;
	//����Ƕ�ײ��
	private int transDeep;

	int getCommitCount() {
		return commitCount;
	}

	void setCommitCount(int commitCount) {
		this.commitCount = commitCount;
	}

	Connection getConnection() {
		return connection;
	}

	void setConnection(Connection conn) {
		this.connection = conn;
	}

	public int getTransCount() {
		return transCount;
	}

	void setTransCount(int transCount) {
		this.transCount = transCount;
	}

	int getTransDeep() {
		return transDeep;
	}

	void setTransDeep(int transDeep) {
		this.transDeep = transDeep;
	}

	/**
	 * �ж������Ƿ���ȫ�ύ��
	 * ͨ���ύ���������������ж������Ƿ���ȫ�ύ��
	 * @return
	 */
	boolean hasFullExecute() {
		return commitCount + 1 == transCount;
	}
}
