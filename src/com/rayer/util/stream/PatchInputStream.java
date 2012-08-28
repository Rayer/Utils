package com.rayer.util.stream;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is a workaround to fix some JPG download from internet can't show in some device.
 * @author rayer
 *
 */
public class PatchInputStream extends FilterInputStream {
	public PatchInputStream(InputStream in) {
		super(in);
	}

	@Override
	public long skip(long n) throws IOException {
		long m = 0L;
		while (m < n) {
			long _m = in.skip(n - m);
			if (_m == 0L)
				break;
			m += _m;
		}
		return m;
	}

}