package cn.tuyucheng.taketoday.batch.partitioner;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class CustomMultiResourcePartitioner implements Partitioner {

	private static final String DEFAULT_KEY_NAME = "fileName";

	private static final String PARTITION_KEY = "partition";

	private Resource[] resources = new Resource[0];

	private String keyName = DEFAULT_KEY_NAME;

	/**
	 * The resources to assign to each partition. In Spring configuration you
	 * can use a pattern to select multiple resources.
	 *
	 * @param resources the resources to use
	 */
	public void setResources(Resource[] resources) {
		this.resources = resources;
	}

	/**
	 * The name of the key for the file name in each {@link ExecutionContext}.
	 * Defaults to "fileName".
	 *
	 * @param keyName the value of the key
	 */
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	/**
	 * Assign the filename of each of the injected resources to an
	 * {@link ExecutionContext}.
	 *
	 * @see Partitioner#partition(int)
	 */
	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		Map<String, ExecutionContext> map = new HashMap<String, ExecutionContext>(gridSize);
		int i = 0, k = 1;
		for (Resource resource : resources) {
			ExecutionContext context = new ExecutionContext();
			Assert.state(resource.exists(), "Resource does not exist: " + resource);
			context.putString(keyName, resource.getFilename());
			context.putString("opFileName", "output" + k++ + ".xml");

			map.put(PARTITION_KEY + i, context);
			i++;
		}
		return map;
	}

}
