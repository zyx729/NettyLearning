package com.zyx.gprc;

import com.zyx.proto.*;
import io.grpc.stub.StreamObserver;

import java.util.UUID;

public class StudentServiceImpl extends StudentServiceGrpc.StudentServiceImplBase {

    /**
     * 在定义proto文件时，这个service是有返回值的，但是这里却是void的
     * 实际上返回给客户端是依赖于responseObserver这个对象来进行的
     * 它包含了三个On的回调方法：
     * responseObserver.onCompleted():方法调用结束的时候干什么，只能调用一次
     * responseObserver.onError():方法异常了干什么
     * responseObserver.onNext():接下来要做什么
     * @param request
     * @param responseObserver
     */
    @Override
    public void getRealNameByUsername(MyRequest request, StreamObserver<MyResponse> responseObserver) {
        System.out.println("接收到客户端信息：" + request.getUsername());

        responseObserver.onNext(MyResponse.newBuilder().setRealname("张三").build());
        responseObserver.onCompleted();
    }

    @Override
    public void getStudentsByAge(StudentRequest request, StreamObserver<StudentResponse> responseObserver) {
        System.out.println("接收到客户端信息：" + request.getAge());
        // 组装流式返回
        responseObserver.onNext(StudentResponse.newBuilder().setAge(20).setName("张三").setCity("北京").build());
        responseObserver.onNext(StudentResponse.newBuilder().setAge(30).setName("李四").setCity("北京1").build());
        responseObserver.onNext(StudentResponse.newBuilder().setAge(40).setName("王五").setCity("北京2").build());
        responseObserver.onNext(StudentResponse.newBuilder().setAge(50).setName("赵六").setCity("北京3").build());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<StudentRequest> getStudentsWrapperByAges(StreamObserver<StudentResponseList> responseObserver) {
        return new StreamObserver<StudentRequest>() {
            @Override
            public void onNext(StudentRequest value) {
                System.out.println("onNext:" + value.getAge());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());
            }

            // 这个方法在这里很重要，表示当客户端的流式信息全部发送给服务器端后，服务器端要把结果返回给客户
            @Override
            public void onCompleted() {
                StudentResponse studentResponse = StudentResponse.newBuilder().setName("张三").setAge(20).setCity("北京").build();
                StudentResponse studentResponse1 = StudentResponse.newBuilder().setName("李四").setAge(30).setCity("北京").build();
                StudentResponseList studentResponseList = StudentResponseList.newBuilder().
                        addStudentResponse(studentResponse).addStudentResponse(studentResponse1).build();

                responseObserver.onNext(studentResponseList);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<StreamRequest> biTalk(StreamObserver<StreamResponse> responseObserver) {
        return new StreamObserver<StreamRequest>() {
            @Override
            public void onNext(StreamRequest value) {
                System.out.println("onNext:" + value.getRequestInfo());
                // 因为是双向流式 所以请求来一个我们这里就回一个
                responseObserver.onNext(StreamResponse.newBuilder().setResponseInfo(UUID.randomUUID().toString()).build());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());
            }

            @Override
            public void onCompleted() {
                // 因为是双向流，两个流是独立的，所以一端关闭之后另一端也最好关闭，不然单向流在这个时候的存在其实是多余的
                // 另外要说的一个是与前面一个方法，在这个位置的区别
                // 因为前一个方法是返回一个单个的对象，所以只有当请求的流全部结束之后才能进行返回，全部的流结束之后才会触发onCompleted方法
                // 所以在前一个方法的这个位置我们的返回代码全部集中写在了这里
                responseObserver.onCompleted();
            }
        };
    }
}
