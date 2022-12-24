# effective-java-2
effective java 학습 repository




<details markdown="1">
<summary>

#### ***item 18. 상속보다는 컴포지션을 사용하라.***

</summary>


- 패키지 경계를 넘어 다른 패키지의 구체 클래스를 상속하는 일은 위험하다.
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




</detail>
