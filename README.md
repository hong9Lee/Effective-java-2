# effective-java-2
effective java 학습 repository




<details markdown="1">
<summary>

#### ***item 18. 상속보다는 컴포지션을 사용하라.***</summary>  

> 패키지 경계를 넘어 다른 패키지의 구체 클래스를 상속하는 일은 위험하다.  
상위 클래스에서 제공하는 메서드 구현이 바뀐다면,,,?  
상위 클래스에서 새로운 메서드가 생긴다면,,,?  

- 컴포지션  
새로운 클래스를 만들고 private 필드로 기존 클래스의 인스턴스를 참조.  
새 클래스의 인스턴스 메서드들은 기본 클래스에 대응하는 메서드를 호출해 그 결과를 반환한다.  
기존 클래스의 구현이 바뀌거나, 새로운 메서드가 생기더라도 아무런 영향을 받지 않는다.  

```
상속을 잘못 사용한 예)
// 상위 클래스 addAll의 구현이 바뀐다면?
// 캡슐화가 제대로 안되어 있다.
@Override
public boolean addAll(Collection<? extends E> c) {
  addCount += c.size();
  return super.addAll(c);
}


// 접근지정자가 다른 같은 이름의 메서드를 상위 클래스에서 선언한다면 문제가 생긴다. 
public int getAddCount() { return addCount; }
```

```
위 예제의 문제를 해결하기위해 컴포지션을 활용하자.  
기존 클래스를 확장하는 것이 아니라, 재사용하고싶은 기능들을 모두 가지고 있는 클래스를 멤버 변수로 선언한다.
모든 메서드가 그 필드를 통해서 전달해야한다.
그래야 사이드 이팩트가 생기지 않는다.

전달 클래스, 포워딩 클래스, 래퍼 클래스 등으로 부른다.
이 자체를 데코레이터 패턴으로 볼 수도 있다.

public class ForwardingSet<E> implements Set<E> {
    private final Set<E> s;

    public ForwardingSet(Set<E> s) {
        this.s = s;
    }

    public void clear() {
        s.clear();
    }
    ...
}


포워딩 클래스 사용
public class InstrumentedSet<E> extends ForwardingSet<E> {
,,,
InstrumentedSet<String> s = new InstrumentedSet<>(new HashSet<>());
s.addAll(List.of("틱", "탁탁", "펑"));
,,,
```
</details>  



  



<details markdown="2">
<summary>

#### ***item 29. 이왕이면 제네릭 타입으로 만들라.***</summary>


### 배열을 사용하는 코드를 제네릭으로 만들 때 해결책 두가지.
> 첫번째 방법 : 제네릭 배열(E[]) 대신에 Object 배열을 생성한 뒤에 제네릭 배열로 형변환 한다.  
형변환을 배열 생성 시 한번만 하며 가독성이 좋다. 하지만, 힙 오염이 발생할 수 있다.  

> 두번째 방법 : 제네릭 배열 대신에 Object 배열을 사용하고, 배열이 반환한 원소를 E로 형변환한다.  
단점은, 원소를 읽을 때 마다 형변환을 해줘야 한다.


1. Object를 이용한 Custom Stack Class
해당 Stack을 사용할 때 형변환을 해줘야 한다.  
`ex) System.out.println(((String)stack.pop()).toUpperCase());`
```
public class Stack {
  private Object[] elements;
    
  public Stack() { elements = new Object[DEFAULT_INITIAL_CAPACITY]; }
  ...
}
```


2. E[]를 이용한 제네릭 스택
형변환은 배열을 만들때 딱 한번만 해주면 된다.
단점은 힙오염이 발생할 수 있다.

* 힙오염 : 주로 매개변수화 타입의 변수가 타입이 다른 객체를 참조할 때 발생한다. ClassCastException 발생.
```
// 이 배열의 런타임 타입은 E[]가 아닌 Object[]다!
public class Stack<E> {
  private E[] elements;
  
  @SuppressWarnings("unchecked")
  public Stack() { elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY]; }
  ...
}

System.out.println(stack.pop().toUpperCase());
```

3. 2번 방법은 힙오염이 발생할 수 있다.
꺼낼 때 제네릭으로 형변환 해준다.
단점은 element를 꺼낼때 마다 형변환을 해줘야 한다. 
```
public class Stack<E> {
    private Object[] elements;
    
    public Stack() { elements = new Object[DEFAULT_INITIAL_CAPACITY]; }
    
    public void push(E e) { 
    ,,, 
    
    public E pop() {
    @SuppressWarnings("unchecked") 
    E result = (E) elements[--size];
```


### 한정적 타입 매개변수
매개변수화 타입을 특정한 타입으로 한정짓고 싶을 때 사용할 수 있다.
- `<E extends Number>` 선언할 수 있는 제네릭 타입을 Number를 상속했거나 구현한 클래스로 제한한다.  

제한한 타입의 인스턴스를 만들거나, 메서드를 호출할 수도 있다.
- `<E extends Number>, Number` 타입이 제공하는 메서드를 사용할 수 있다.  

다수의 타입으로 한정할 수 있다. 이때 클래스 타입을 가장 먼저 선언해야 한다.  
- `<E extends Number & Serializable>` 선언할 제네릭 타입은 Integer와 Number를 모두 상속 또는 구현한 타입이어야 한다.  
</details>









<details markdown="3">
<summary>

#### ***item 30. 이왕이면 제네릭 메서드로 만들라.***</summary>  

매개변수화 타입을 받는 정적 유틸리티 메서드  
- 한정적 와일드카드 타입을 사용하면 더 유연하게 개선할 수 있다.  

제네릭 싱글턴 팩토리  
- (소거 방식이기 떄문에)불변 객체 하나를 어떤 타입으로든 매개변수화 할 수 있다.  

재귀적 타입 한정  
- 자기 자신이 들어간 표현식을 사용하여 타입 매개변수의 허용 범위를 한정한다.  


1. 제네릭의 타입을 생략하지 말고 명시적으로 사용한다면 컴파일타임에 오류를 검출할 수 있다.
```
public static <E> Set<E> union(Set<E> s1, Set<E> s2) ,,,
```

2. 제네릭 싱글턴 팩토리를 사용해 특정 타입으로 매개변수화 할 수 있다.  
UnaryOperator 함수형 인터페이스를 사용하면 타입별 팩토리를 생성하지 않아도 된다.
```
-- old
public static Function<String, String> stringIdentityFunction() { return (t) -> t; }
public static Function<Number, Number> integerIdentityFunction() { return (t) -> t; }

Function<String, String> sameString = stringIdentityFunction();
  for (String s : strings)
    System.out.println(sameString.apply(s));
Function<Number, Number> sameNumber = integerIdentityFunction();
,,,

--new
private static UnaryOperator<Object> IDENTITY_FN = (t) -> t;

@SuppressWarnings("unchecked")
public static <T> UnaryOperator<T> identityFunction() { return (UnaryOperator<T>) IDENTITY_FN; }

UnaryOperator<String> sameString = identityFunction();
for (String s : strings)
            System.out.println(sameString.apply(s));
UnaryOperator<Number> sameNumber = identityFunction();
,,,

```

3. 재귀적 타입 한정을 이용해 상호 비교할 수 있다.
```
public static <E extends Comparable<E>> E max(Collection<E> c) ,,,
```
</details>










