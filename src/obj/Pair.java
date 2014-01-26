package obj;

/**
 * 
 * @author BURST project
 *
 * @param <K>
 * @param <V>
 */
public class Pair<K, V> {
	public Pair(K iK, V iV)
	{
		m_key = iK;
		m_value = iV;
	}
	
	
	public K key()
	{
		return m_key;
	}
	
	
	public V value()
	{
		return m_value;
	}
	
	
	@Override
	public String toString()
	{
		StringBuffer s = new StringBuffer(m_key != null ? m_key.toString() : "null");
		
		s.append(';').append(m_value != null ? m_value.toString() : "null");
		
		return s.toString();
	}
		
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m_key == null) ? 0 : m_key.hashCode());
		result = prime * result + ((m_value == null) ? 0 : m_value.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		Pair other = (Pair) obj;
		if (m_key == null) {
			if (other.m_key != null)
				return false;
		} else if (!m_key.equals(other.m_key))
			return false;
		if (m_value == null) {
			if (other.m_value != null)
				return false;
		} else if (!m_value.equals(other.m_value))
			return false;
		return true;
	}

	private K m_key;
	private V m_value;
}

