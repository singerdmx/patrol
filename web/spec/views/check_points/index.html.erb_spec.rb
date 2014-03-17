require 'spec_helper'

describe "check_points/index" do
  before(:each) do
    assign(:check_points, [
      stub_model(CheckPoint),
      stub_model(CheckPoint)
    ])
  end

  it "renders a list of check_points" do
    render
    # Run the generator again with the --webrat flag if you want to use webrat matchers
  end
end
