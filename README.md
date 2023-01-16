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

#### ***item 19. 상속을 고려해 설계하고 문서화하라. 그러지 않았다면 상속을 금지하라.***</summary>  


상속용 클래스는 내부 구현을 문서로 남겨야 한다.  
- @implSpec을 사용할 수 있다.  
  
내부 동작 중간에 끼어들 수 있는 훅(hook)을 잘 선별하여 Protected 메서드로 공개해야 한다.  
상속용으로 설계한 클래스는 배포 전에 반드시 하위 클래스를 만들어 검증해야 한다.  
상속용 클래스의 생성자는 재정의 가능한 메서드를 호출해서는 안된다.  
- Cloneable과 Serializable을 구현할때 조심해야 한다.  
상속용으로 설계한 클래스가 아니라면 상속을 금지한다.  
- final 클래스 또는 Private 생성자.  

</details>
  





<details markdown="2">
<summary>

#### ***item 20. 추상 클래스보다 인터페이스를 우선하라.***</summary>  

자바 8부터 인터페이스도 ***디폴트 메서드***를 제공할 수 있다.  
기존 클래스도 손쉽게 새로운 인터페이스를 구현해 넣을 수 있다.  
인터페이스는 믹스인(mixin) 정의에 안성맞춤이다. (선택적인 기능 추가)  
계층구조가 없는 타입 프레임워크를 만들 수 있다.  
래퍼 클래스와 함께 사용하면 인터페이스는 기능을 향상 시키는 안전하고 강력한 수단이 된다.  
구현이 명백한 것은 인터페이스의 디폴트 메서드를 사용해 프로그래머의 일감을 덜어 줄 수 있다.  

```
// 사용 가능한 경우엔 default나 static을 사용하여 인터페이스를 계속 진화시킬 수 있다.
public interface TimeClient {

default ZonedDateTime getZonedDateTime(String zoneString) {
  return ZonedDateTime.of(getLocalDateTime(), getZonedId(zoneString));
}

static ZoneId getZonedId(String zoneString) {
  try {
    return ZoneId.of(zoneString);
  ,,,
```


#### 인터페이스와 추상 골격 클래스  
  
인터페이스와 추상 클래스의 장점을 모두 취할 수 있다.  
- 인터페이스 - 디폴트 메서드 구현  
- 추상 골격 클래스 - 나머지 메서드 구현  
- 템플릿 메서드 패턴  
다중 상속을 시뮬레이트 할 수 있다.  
골격 구현은 상속용 클래스이다.  

```
public class MyCat extends AbstractCat implements Flyable {

    private MyFlyable myFlyable = new MyFlyable();
    
    
    @Override
    public void fly() {
        this.myFlyable.fly();
    }
    
    ,,,
    
    private class MyFlyable extends AbstractFlyable {
        @Override
        public void fly() {
            System.out.println("날아라.");
        }
    }
```

#### 템플릿 메서드 패턴  
알고리즘 구조를 서브 클래스가 확장할 수 있도록 템플릿으로 제공하는 방법.  
- 추상 클래스는 템플릿을 제공하고 하위 클래스는 구체적인 알고리즘을 제공한다.  




</details>





<details markdown="3">
<summary>

#### ***item 21. 인터페이스는 구현하는 쪽을 생각해 설계하라.***</summary>  

기존 인터페이스에 디폴트 메서드 구현을 추가하는 것은 위험한 일이다.  
- 디폴트 메서드는 구현 클래스에 대해 아무것도 모른 채 합의 없이 무작정 "삽입" 될 뿐이다.  
- 디폴트 메서드는 기존 구현체에 런타임 오류를 일으킬 수 있다.  
인터페이스를 설계할 때는 세심한 주의를 기울여야 한다.  
- 서로 다른 방식으로 최소한 세 가지는 구현을 해보자.  

#### ConcurrentModificationException  
현재 바뀌면 안되는 것을 수정할 때 발생하는 예외  
- List.of 로 만들어진 List는 ImmutableCollections으로 만들어졌기 때문에 요소를 변경할 수 없다.  
- collection을 for문을 사용하여 순회하는 중에는 remove로 제거하지말고 이터레이터, 인덱스, removeIf 등의 방법을 사용하여 요소를 제거할 수 있다.

```
// 이터레이터의 remove 사용하기
for (Iterator<Integer> iterator = numbers.iterator(); iterator.hasNext();) {
  Integer integer = iterator.next();
  if(integer == 3) {
    iterator.remove();
  }
}

// 인덱스 사용하기
for (int i = 0; i < numbers.size() ; i++) {
if (numbers.get(i) == 3) {
    numbers.remove(numbers.get(i));
  }
}

// removeIf 사용하기
numbers.removeIf(number -> number == 3);
```
</details>




<details markdown="3">
<summary>

#### ***item 22. 인터페이스는 타입을 정의하는 용도로만 사용하라.***</summary>  
  
상수를 정의하는 용도로 인터페이스를 사용하지 말 것.  
- 클래스 내부에서 사용할 상수는 내부 구현에 해당한다.  
- 내부 구현을 클래스의 API로 노출하는 행위가 된다.  
- 클라이언트에 혼란을 준다.  
  
상수를 정의하는방법.  
- 특정 클래스나 인터페이스.  
- 열거형.  
- 인스턴스화 할 수 없는 유틸리티 클래스  
  
`특정한 함수에서만 사용하는 상수라면 해당 클래스로 옮겨서 사용한다.`  
`공용으로 사용되는 상수라면 상수 유틸리티 클래스로 옮겨서 사용한다.`  
`자바7 이후 버전부터 _는 숫자 리터럴의 어디에도 등장할 수 있다. 이로 인해 숫자를 끊어 읽을 수 있게 되어 가독성을 향상 시킬 수 있다. `  
</details>



<details markdown="3">
<summary>

#### ***item 23. 태그 달린 클래스보다는 클래스 계층 구조를 활용하라.***</summary>  
  
  
태그 달린 클래스 : 그 클래스가 가지고 있는 필드중의 일부가 클래스의 구체적인 타입을 나타내는 경우.  
- 쓸데없는 코드가 많다.  
- 가독성이 나쁘다.  
- 메모리도 많이 사용한다.  
- 필드를 final로 선언하려면 불필요한 필드까지 초기화해야 한다.  
- 인스턴스 타입만으로는 현재 나타내는 의미를 알 길이 없다.  

#### 클래스 계층 구조로 바꾸면 모든 단점을 해결할 수 있다.  



`Shape이라는 태그다 달린 Figure 클래스`  
`하나의 클래스에 여러가지 기능들을 담고 있는 문제가 있다`
```
class Figure {
    enum Shape { RECTANGLE, CIRCLE, SQUARE };
    final Shape shape;
  
    Figure(double radius) {
        shape = Shape.CIRCLE;
        this.radius = radius;
    }
    ,,,   
```

`상속을 통한 계층구조로 변환한다`  
```
class Rectangle extends Figure {
  
  @Override double area() { return length * width; }
  ,,,
}  
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










<details markdown="4">
<summary>

#### ***item 31. 한정적 와일드카드를 사용해 API 유연성을 높이라.***</summary>  

한정적 타입 : `Iterable<E extends Number>`  
한정적 와일드 카드 : `Iterable<? extends E>`  


### producer
```
생산자(producer) 매개변수에 와일드카드 타입 적용하여 유연성을 높일 수 있다.
public void pushAll(Iterable<? extends E> src) {
  for (E e : src)
        push(e);
}

// Number 하위 타입을 넣을 수 있다.
Stack<Number> numberStack = new Stack<>();

Iterable<Integer> integers = Arrays.asList(3, 1, 4, 1, 5, 9);
numberStack.pushAll(integers);

Iterable<Double> doubles = Arrays.asList(3.1, 1.0, 4.0, 1.0, 5.0, 9.0);
numberStack.pushAll(doubles);
```

### consumer
```
소비자(consumer) 매개변수에 와일드카드 타입 적용하여 유연성을 높일 수 있다.
public void popAll(Collection<? super E> dst) {
  while (!isEmpty())
      dst.add(pop());
}

// 상위 타입을 허용한다. (Object는 Number의 super)
Collection<Object> objects = new ArrayList<>();
numberStack.popAll(objects);
```


### 와일드카드 활용 팁  
메서드 선언에 타입 매개변수가 한번만 나오면 와일드카드로 대체하라.  
- 한정적 타입이라면 한정적 와일드카드로  
- 비한정적 타입이라면 비한정적 와일드카드로 es) List<?>  
  
**비한정적 와일드카드로 정의한 타입에는 null을 제외한 아무것도 넣을 수 없다.**  
**consumer만 존재하는 경우 비한정적 타입을 사용할 수 있지만,  producer에서는 사용하기 좋지 않다.**



### 타입 추론  
- 타입을 명시하지 않아도 자바 컴파일러가 어떤 타입을 쓸지 알아내는 것.  

> 타입을 추론하는 컴파일러의 기능  
모든 인자의 가장 구체적인 공통 타입  
제네릭 메서드와 타입 추론: 메서드 매개변수를 기반으로 타입 매개변수를 추론할 수 있다.  
제네릭 클래스 생성자를 호출할 때 다이아몬드 연산자 <>를 사용하면 타입을 추론한다.  
자바 컴파일러는 "타겟 타입"을 기반으로 호출하는 제네릭 메서드의 타입 매개변수를 추론한다.  
-> 자바 8에서 "타겟 타입"이 "메서드의 인자"까지 확장되면서 이전에 비해 타입 추론이 강화되었다.

`ArrayList<Box<Integer>> listOfIntegerBoxes = new ArrayList<>();`  
`BoxExample.<Integer>addBox(10, listOfIntegerBoxes);` // 명시적 타입 인수  
  
```
// Target Type을 보고 타입을 추론하게 된다.
// 자바8에서부터 범위가 확장된다. (메서드의 인자 타입까지)
List<String> stringlist = Collections.emptyList();
List<Integer> integerlist = Collections.emptyList();


BoxExample.processStringList(Collections.emptyList());
private static void processStringList(List<String> stringList) {} // 알아서 타입 추론해준다.
```
</details>






<details markdown="5">
<summary>

#### ***item 32. 제네릭과 가변인수를 함께 쓸 때는 신중하라.***</summary>  
  
제네릭 가변인수 배열에 값을 저장하는 것은 안전하지 않다.
- 힙 오염이 발생할 수 있다. (컴파일 경고 발생)  
- 자바7에 추가된 @SafeVarargs 어노테이션을 사용할 수 있다.  
  
제네릭 가변인수 배열의 참조를 밖으로 노출하면 힙 오염을 전달할 수 있다.  
- 예외적으로, @SafeVarargs를 사용한 메서드에 넘기는 것은 안전하다.  
- 예외적으로, 배열 내용의 일부 함수를 호출하는 일반 메서드로 넘기는 것은 안전하다.  
  
가변인수를 List로 바꾼다면,,,  
- 배열없이 제네릭만 사용하므로 컴파일러가 타입 안정성을 보장할 수 있다.  
- @SafeVarargs 어노테이션을 사용할 필요가 없다.  
- 실수로 안전하다고 판단할 걱정도 없다.  


### ThreadLocal  
  
  
모든 멤버 변수는 기본적으로 여러 쓰레드에서 공유해서 쓰일 수 있다.  
이때 쓰레드 안전성과 관련된 여러 문제가 발생할 수 있다.  
- 경합 또는 경쟁조건 (Racce-Condition)  
- 교착상태 (deadlock)  
- Livelock  
  
쓰레드 지역 변수를 사용하면 동기화를 하지 않아도 한 쓰레드에서만 접근 가능한 값이기 때문에 안전하게 사용할 수 있다.  
한 쓰레드 내에서 공유하는 데이터로, 메서드 매개변수에 매번 전달하지 않고 전역 변수처럼 사용할 수 있다.  


</details>






