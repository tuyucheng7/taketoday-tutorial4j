package cn.tuyucheng.taketoday.mapper;

import cn.tuyucheng.taketoday.dto.TransactionDTO;
import cn.tuyucheng.taketoday.entity.Transaction;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Mapper
abstract class TransactionMapper {

	public TransactionDTO toTransactionDTO(Transaction transaction) {
		TransactionDTO transactionDTO = new TransactionDTO();
		transactionDTO.setUuid(transaction.getUuid());
		transactionDTO.setTotalInCents(transaction.getTotal().multiply(new BigDecimal("100")).longValue());
		return transactionDTO;
	}

	public abstract List<TransactionDTO> toTransactionDTO(Collection<Transaction> transactions);
}
