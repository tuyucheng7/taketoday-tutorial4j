package cn.tuyucheng.taketoday.chooseapi.grpc;

import cn.tuyucheng.taketoday.chooseapi.BooksServiceOuterClass;
import cn.tuyucheng.taketoday.chooseapi.dtos.Book;
import cn.tuyucheng.taketoday.chooseapi.services.BooksService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
public class BooksServiceGrpc extends cn.tuyucheng.taketoday.chooseapi.BooksServiceGrpc.BooksServiceImplBase {

	private final BooksService booksService;

	public BooksServiceGrpc(BooksService booksService) {
		this.booksService = booksService;
	}

	@Override
	public void books(BooksServiceOuterClass.BooksRequest request, StreamObserver<BooksServiceOuterClass.BooksResponse> responseObserver) {
		List<Book> books = booksService.getBooks();
		BooksServiceOuterClass.BooksResponse.Builder responseBuilder = BooksServiceOuterClass.BooksResponse.newBuilder();
		books.forEach(book -> responseBuilder.addBook(GrpcBooksMapper.mapBookToProto(book)));
		responseObserver.onNext(responseBuilder.build());
		responseObserver.onCompleted();
	}
}